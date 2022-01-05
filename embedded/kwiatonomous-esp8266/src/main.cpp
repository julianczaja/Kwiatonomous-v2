#include <Arduino.h>
#include <ESP8266WiFi.h>
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
#include "KwiatonomousApi.h"

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

#define DEVICE_ID "---"

void lowBatteryCallback();
void onError(char *message);
void goToSleep(unsigned long sleepTime);
void connectToWifi(WiFiConfiguration *wifiConfiguration);
unsigned long getEpochTime();

WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org");
Adafruit_AHTX0 aht;
Led ledBuiltin = Led(LED_BUILTIN_PIN);
Led ledInfo = Led(LED_BLUE_PIN);
WateringManager wateringManager = WateringManager(PUMP_PIN);
DataManager dataManager = DataManager();
KwiatonomousApi kwiatonomousApi = KwiatonomousApi();
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
  delay(200);

  // Get battery status before Wi-Fi is on
  ledInfo.on(20);
  batteryManager.update();
  ledInfo.off();

  // Connect to Wi-Fi
  WiFiConfiguration wifiConfiguration = dataManager.getWiFiConfiguration();
  connectToWifi(&wifiConfiguration);

  ledInfo.on(10);
  kwiatonomousApi.init((char *) DEVICE_ID);

  DeviceUpdate deviceUpdate = DeviceUpdate();
  deviceUpdate.batteryLevel = batteryManager.getBatteryLevel();
  deviceUpdate.batteryVoltage = batteryManager.getBatteryVoltage();

  // Get temperature and humidity from AHT sensor
  if (aht.begin())
  {
    sensors_event_t sensor_temperature, sensor_humidity;
    aht.getEvent(&sensor_humidity, &sensor_temperature);
    deviceUpdate.temperature = sensor_temperature.temperature;
    deviceUpdate.humidity = sensor_humidity.relative_humidity;
  }
  else
  {
    Serial.println("Couldn't find AHT. Check wiring!");
  }

  // Get current time from NTP server
  deviceUpdate.epochTime = getEpochTime();

  // Get device configuration
  DeviceConfiguration configuration = DeviceConfiguration();
  bool getDeviceConfigurationSuccess = kwiatonomousApi.getDeviceConfiguration(&configuration);
  if (getDeviceConfigurationSuccess == false)
  {
    onError((char *)"Can't get device configuration");
  }

  // Get next watering info
  bool getNextWateringSuccess = kwiatonomousApi.getNextWatering(&(wateringManager.nextWatering));
  if (getNextWateringSuccess == false)
  {
    onError((char *)"Can't get next watering");
  }

  // Update watering manager
  Serial.println("\n> Watering manager update");
  wateringManager.update(configuration, deviceUpdate.epochTime);
  if (wateringManager.nextWateringUpdated == true)
  {
    if (kwiatonomousApi.updateNextWatering(wateringManager.nextWatering) == false)
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
  if (kwiatonomousApi.sendUpdate(&deviceUpdate) == false)
  {
    onError((char *)"Can't send device update");
  }

  // Finish http connection
  delay(500);
  kwiatonomousApi.end();
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
      onError((char *)"Can't connect to Wi-Fi");
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
      onError((char *)"Something is wrong with epochTime (year > 2035)");
    }
  }
  timeClient.end();

  return epochTime;
}

void lowBatteryCallback()
{
  Serial.println("LOW BATTERY!!!");
  ledInfo.signalizeLowBattery();
}

void onError(char *message) 
{
  Serial.print("Error! Message: ");
  Serial.println(message);
  dataManager.increaseFailuresCount();
  ledInfo.signalizeLowBattery();
  kwiatonomousApi.end();
  goToSleep(SLEEP_TIME_ERROR);
}

void goToSleep(unsigned long sleepTime)
{
  // ESP.deepSleep(SLEEP_TIME_NORMAL - micros());
  ESP.deepSleepInstant(sleepTime - micros() + SLEEP_CORRECTION_TIME, RF_NO_CAL);
}
