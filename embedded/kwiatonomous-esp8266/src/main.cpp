#include <Arduino.h>
#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <WiFiClient.h>
#include <WiFiUdp.h>
#include <NTPClient.h>
#include <Adafruit_AHTX0.h>

#include "Led.h"
#include "BatteryManager.h"
#include "WateringManager.h"
#include "DataManager.h"
#include "DeviceConfiguration.h"
#include "WiFiConfiguration.h"
#include "DeviceUpdate.h"

#define LED_BLUE_PIN 15
#define LED_BUILTIN_PIN 2
#define VOLTAGE_DIVIDER_PIN 14
#define PUMP_PIN 12
#define BATTERY_VOLTAGE_PIN A0

#define SLEEP_CORRECTION_TIME 25e5
#define SLEEP_TIME_NORMAL 30 * 60e6  // us
#define SLEEP_TIME_ERROR 15 * 60e6   // us
#define WIFI_CONNECTION_TIMEOUT 15e3 // ms
#define NTP_TIMEOUT 10e3             // ms

// TODO: Hide in local file
#define DEVICE_ID "---"
#define SERVER_NAME "---"
#define POST_UPDATE_FORMAT "{\"timestamp\":%lu,\"batteryLevel\":%d,\"batteryVoltage\":%g,\"temperature\":%g,\"humidity\":%g}"
#define GET_CONFIGURATION_FORMAT "{\"sleepTimeMinutes\":%d,\"wateringOn\":%d,\"wateringIntervalDays\":%d,\"wateringAmount\":%d,\"wateringTime\":%s}"

void lowBatteryCallback();
void goToSleep(unsigned long sleepTime);
void connectToWifi(WiFiConfiguration *wifiConfiguration);
unsigned long getEpochTime();
bool getDeviceConfiguration(DeviceConfiguration *configuration);
bool getNextWatering(unsigned long *nextWatering);
bool updateNextWatering(unsigned long newNextWatering);
bool sendUpdate(DeviceUpdate *deviceUpdate);

WiFiUDP ntpUDP;
HTTPClient http;
WiFiClient client;
NTPClient timeClient(ntpUDP, "pool.ntp.org");
Adafruit_AHTX0 aht;
Led ledBuiltin = Led(LED_BUILTIN_PIN);
Led ledInfo = Led(LED_BLUE_PIN);
WateringManager wateringManager = WateringManager(PUMP_PIN);
DataManager dataManager = DataManager();
BatteryManager batteryManager = BatteryManager(BATTERY_VOLTAGE_PIN,
                                               VOLTAGE_DIVIDER_PIN,
                                               25,
                                               20,
                                               &lowBatteryCallback);



void setup()
{
  Serial.begin(115200);
  Serial.println("\nHello world!\n\n");

  ledBuiltin.init(true);
  ledInfo.init(false);
  batteryManager.init();
  wateringManager.init();
  dataManager.init();
  delay(250);

  // Get battery status before Wi-Fi is on
  ledInfo.on(20);
  batteryManager.update();
  ledInfo.off();

  // Connect to Wi-Fi
  WiFiConfiguration wifiConfiguration = dataManager.getWiFiConfiguration();
  connectToWifi(&wifiConfiguration);

  ledInfo.on(10);

  // Get temperature and humidity from AHT sensor
  float temperature = 0.0f;
  float humidity = 0.0f;
  if (aht.begin())
  {
    sensors_event_t sensor_temperature, sensor_humidity;
    aht.getEvent(&sensor_humidity, &sensor_temperature);
    temperature = sensor_temperature.temperature;
    humidity = sensor_humidity.relative_humidity;
  }
  else
  {
    Serial.println("Couldn't find AHT. Check wiring!");
  }

  // Get current time from NTP server
  unsigned long epochTime = getEpochTime();

  // Configure http client
  http.setReuse(false);
  http.setTimeout(3000);

  // Get device configuration
  DeviceConfiguration configuration = DeviceConfiguration();
  bool getDeviceConfigurationSuccess = getDeviceConfiguration(&configuration);
  if (getDeviceConfigurationSuccess == false)
  {
    ledInfo.signalizeError();
    goToSleep(SLEEP_TIME_ERROR);
  }

  // Get next watering info
  bool getNextWateringSuccess = getNextWatering(&(wateringManager.nextWatering));
  if (getNextWateringSuccess == false)
  {
    ledInfo.signalizeError();
    goToSleep(SLEEP_TIME_ERROR);
  }

  // Update watering manager
  Serial.println("\n> Watering manager update");
  wateringManager.update(configuration, epochTime);
  if (wateringManager.nextWateringUpdated == true)
  {
    if (updateNextWatering(wateringManager.nextWatering) == false)
    {
      //                  TODO
      // Important part - update next watering time
      // If the time won't be updated, that plant will
      // be watered on each wake up
      //
      // Maybe save info about failure to EEPROM?
    }
  }

  // Send device update
  DeviceUpdate deviceUpdate = DeviceUpdate();
  deviceUpdate.epochTime = epochTime;
  deviceUpdate.batteryLevel = batteryManager.getBatteryLevel();
  deviceUpdate.batteryVoltage = batteryManager.getBatteryVoltage();
  deviceUpdate.temperature = temperature;
  deviceUpdate.humidity = humidity;
  sendUpdate(&deviceUpdate);

  // Finish http connection
  delay(500);
  http.end();
  delay(500);

  // Everything done, go to sleep
  Serial.println("Going to sleep...");
  ledInfo.off();
  goToSleep(SLEEP_TIME_NORMAL);
}

void loop() {}

void connectToWifi(WiFiConfiguration *wifiConfiguration)
{
  Serial.println("Connecting to Wi-Fi...");
  ledBuiltin.on(5);

  unsigned long *startConnection = (unsigned long *)malloc(sizeof(unsigned long));
  *startConnection = millis();

  WiFi.begin(wifiConfiguration->ssid, wifiConfiguration->password);

  while (WiFi.status() != WL_CONNECTED)
  {
    if ((millis() - *startConnection) > WIFI_CONNECTION_TIMEOUT)
    {
      // If can't connect to Wi-Fi, sleep for some time an try again
      ledInfo.signalizeError();
      goToSleep(SLEEP_TIME_ERROR);
    }
    Serial.print(".");
    delay(100);
  }
  free(startConnection);

  Serial.print("\nConnected with IP: ");
  Serial.println(WiFi.localIP());

  ledBuiltin.off();
}

unsigned long getEpochTime()
{
  Serial.println("\n> getEpochTime");

  timeClient.begin();
  unsigned long *startNtp = (unsigned long *)malloc(sizeof(unsigned long));
  *startNtp = millis();
  bool ntpSuccess = false;
  unsigned long epochTime = 0;
  while ((ntpSuccess = timeClient.update()) != true)
  {
    if ((millis() - *startNtp) > NTP_TIMEOUT)
    {
      break;
    }
    delay(100);
  }
  if (ntpSuccess)
  {
    epochTime = timeClient.getEpochTime();
    Serial.print("timestamp: ");
    Serial.println(epochTime);

    // https://github.com/gmag11/NtpClient/issues/70
    // If error in UDP packet (year bigger than 2035) - sleep
    if (epochTime > 2051222400)
    {
      ledInfo.signalizeError();
      goToSleep(SLEEP_TIME_ERROR);
    }
  }
  timeClient.end();

  return epochTime;
}

bool getNextWatering(unsigned long *nextWatering)
{
  Serial.println("\n> getNextWatering");
  char path[128];
  sprintf(path, "%s/%s/nextwatering", SERVER_NAME, DEVICE_ID);
  http.begin(client, path);
  http.addHeader("Content-Type", "application/json");

  int httpGetResponseCode = http.GET();
  if (httpGetResponseCode == HTTP_CODE_OK)
  {
    Serial.println("Success");
    String in_payload = http.getString();
    char *end;
    *nextWatering = strtoul(in_payload.c_str(), &end, 10);

    Serial.print("Next watering: ");
    Serial.println(*nextWatering);
    return true;
  }
  else
  {
    Serial.print("Error code: ");
    Serial.println(httpGetResponseCode);
    return false;
  }
}

bool updateNextWatering(unsigned long newNextWatering) 
{
  Serial.println("\n> updateNextWatering");
  char path[128];
  sprintf(path, "%s/%s/nextwatering", SERVER_NAME, DEVICE_ID);
  http.begin(client, path);
  http.addHeader("Content-Type", "application/json");

  char payload[sizeof(newNextWatering)];
  sprintf(payload, "%lu", newNextWatering);
  Serial.print("Sending payload: ");
  Serial.println(payload);


  int httpResponseCode = http.POST(payload);
  if (httpResponseCode == HTTP_CODE_OK)
  {
    Serial.println("Success");
    return true;
  }
  else
  {
    Serial.print("Error code: ");
    Serial.println(httpResponseCode);
    return false;
  }
}

bool getDeviceConfiguration(DeviceConfiguration *configuration)
{
  Serial.println("\n> getDeviceConfiguration");
  char path[128];
  sprintf(path, "%s/%s/configuration", SERVER_NAME, DEVICE_ID);
  http.begin(client, path);
  http.addHeader("Content-Type", "application/json");

  int httpGetResponseCode = http.GET();
  if (httpGetResponseCode == HTTP_CODE_OK)
  {
    Serial.println("Success");
    String in_payload = http.getString();
    Serial.print("payload: ");
    Serial.println(in_payload);

    int parametersParsed = sscanf(in_payload.c_str(),
                                  GET_CONFIGURATION_FORMAT,
                                  &(configuration->sleepTimeMinutes),
                                  &(configuration->wateringOn),
                                  &(configuration->wateringIntervalDays),
                                  &(configuration->wateringAmount),
                                  &(configuration->wateringTime));

    if (parametersParsed == 5)
    {
      Serial.printf("sleepTimeMinutes=%d\nwateringOn=%d\nwateringIntervalDays=%d\nwateringAmount=%d\nwateringTime=%s\n",
                    (*configuration).sleepTimeMinutes,
                    (*configuration).wateringOn,
                    (*configuration).wateringIntervalDays,
                    (*configuration).wateringAmount,
                    (*configuration).wateringTime);
    }
    else
    {
      Serial.println("Parsing failed");
      return false;
    }
  }
  else
  {
    Serial.print("Error code: ");
    Serial.println(httpGetResponseCode);
    return false;
  }

  return true;
}

bool sendUpdate(DeviceUpdate *deviceUpdate)
{
  Serial.println("\n> sendUpdate");

  char path[128];
  sprintf(path, "%s/%s/updates", SERVER_NAME, DEVICE_ID);
  http.begin(client, path);
  http.addHeader("Content-Type", "application/json");

  char payload[256];
  sprintf(payload, POST_UPDATE_FORMAT,
          deviceUpdate->epochTime,
          deviceUpdate->batteryLevel,
          deviceUpdate->batteryVoltage,
          deviceUpdate->temperature,
          deviceUpdate->humidity);
  Serial.print("Sending payload: ");
  Serial.println(payload);

  int httpResponseCode = http.POST(payload);
  if (httpResponseCode == HTTP_CODE_OK)
  {
    Serial.println("Success");
    return true;
  }
  else
  {
    Serial.print("Error code: ");
    Serial.println(httpResponseCode);
    return false;
  }
}

void lowBatteryCallback()
{
  Serial.println("LOW BATTERY!!!");
  ledInfo.signalizeLowBattery();
}

void goToSleep(unsigned long sleepTime)
{
  // ESP.deepSleep(SLEEP_TIME_NORMAL - micros());
  ESP.deepSleepInstant(sleepTime - micros() + SLEEP_CORRECTION_TIME, RF_NO_CAL);
}
