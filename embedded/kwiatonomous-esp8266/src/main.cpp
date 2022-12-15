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

#define BATTERY_LOW_THRESHOLD_VOLTAGE 3.3 // V
#define SLEEP_CORRECTION_TIME 25e5        // us
#define SLEEP_TIME_DEFAULT 30 * 60e6      // us
#define WIFI_CONNECTION_TIMEOUT 15e3      // ms
#define NTP_TIMEOUT 10e3                  // ms

#define DEVICE_ID "testid"

void lowBatteryCallback();
void onError(char *message);
void goToSleep(unsigned long sleepTime);
void connectToWifi(WiFiConfiguration *wifiConfiguration);
unsigned long getEpochTime(int16_t timeZoneOffset);

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
  delay(500);

  Serial.print("Failure count: ");
  Serial.println(dataManager.getFailuresCount());

  // Get battery status before Wi-Fi is on
  // If battery too low just go to sleep for max time
  batteryManager.update();
  float currentVoltage = batteryManager.getBatteryVoltage();
  Serial.print("Current voltage: ");
  Serial.println(currentVoltage);
  Serial.print("Current battery level: ");
  Serial.println(batteryManager.getBatteryLevel());
  if (currentVoltage <= BATTERY_LOW_THRESHOLD_VOLTAGE)
  {
    ledInfo.signalizeLowBattery();
    goToSleep(ESP.deepSleepMax());
    return;
  }

  // Connect to Wi-Fi
  WiFiConfiguration wifiConfiguration = WiFiConfiguration();
  dataManager.getWiFiConfiguration(&wifiConfiguration);
  connectToWifi(&wifiConfiguration);

  ledInfo.on(2);
  kwiatonomousApi.init((char *)DEVICE_ID);

  // Get device configuration
  DeviceConfiguration configuration = DeviceConfiguration();
  bool getDeviceConfigurationSuccess = kwiatonomousApi.getDeviceConfiguration(&configuration);
  if (getDeviceConfigurationSuccess == false)
  {
    onError((char *)"Can't get device configuration");
  }

  // Save device configuration to EEPROM
  dataManager.setDeviceConfiguration(&configuration);

  // Get next watering info
  bool getNextWateringSuccess = kwiatonomousApi.getNextWatering(&(wateringManager.nextWatering));
  if (getNextWateringSuccess == false)
  {
    onError((char *)"Can't get next watering");
  }

  DeviceUpdate deviceUpdate = DeviceUpdate();
  deviceUpdate.epochTime = getEpochTime(configuration.timeZoneOffset * 3600);
  deviceUpdate.batteryLevel = batteryManager.getBatteryLevel();
  deviceUpdate.batteryVoltage = batteryManager.getBatteryVoltage();

  // Get temperature and humidity from AHT sensor
  Adafruit_AHTX0 aht;
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

  // Update watering manager
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
    else
    {
      kwiatonomousApi.sendWateringEvent(deviceUpdate.epochTime);
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
  ledInfo.off();
  unsigned long sleepTimeMicros = configuration.sleepTimeMinutes * 60e6;
  goToSleep(sleepTimeMicros);
}

void loop() {}

void connectToWifi(WiFiConfiguration *wifiConfiguration)
{
  Serial.println("Connecting to Wi-Fi...");
  ledBuiltin.on(2);

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

unsigned long getEpochTime(int16_t timeZoneOffset)
{
  Serial.println("\n> getEpochTime");

  WiFiUDP ntpUDP;
  NTPClient timeClient(ntpUDP, "pool.ntp.org");

  timeClient.begin();
  timeClient.setTimeOffset(timeZoneOffset);

  unsigned long startNtp = millis();
  bool ntpSuccess = false;
  unsigned long epochTime = 0;

  while ((ntpSuccess = timeClient.update()) != true)
  {
    if ((millis() - startNtp) > NTP_TIMEOUT)
    {
      break;
    }
    delay(100);
  }

  if (ntpSuccess)
  {
    epochTime = timeClient.getEpochTime();
    Serial.print("Current epoch time: ");
    Serial.println(epochTime);

    // https://github.com/gmag11/NtpClient/issues/70
    // If error in UDP packet (year bigger than 2035) - sleep
    if (epochTime > 2051222400)
    {
      onError((char *)"Something is wrong with epochTime (year > 2035)");
    }
  }
  else
  {
    onError((char *)"Can't get epochTime");
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

  DeviceConfiguration savedDeviceConfiguration = DeviceConfiguration();
  dataManager.getDeviceConfiguration(&savedDeviceConfiguration);

  if (savedDeviceConfiguration.sleepTimeMinutes == 0)
  {
    goToSleep(SLEEP_TIME_DEFAULT);
  }
  else
  {
    // Sleep for half of normal time
    unsigned long sleepTimeMicros = (savedDeviceConfiguration.sleepTimeMinutes * 60e6) / 2;
    goToSleep(sleepTimeMicros);
  }
}

void goToSleep(unsigned long sleepTimeMicros)
{
  Serial.print("\nGoing to sleep for ");
  Serial.print(sleepTimeMicros / 60e6);
  Serial.println(" minutes!");
  Serial.flush();
  Serial.end();

  ESP.deepSleepInstant(sleepTimeMicros - micros(), RF_NO_CAL);
  // ESP.deepSleepInstant(sleepTimeMicros - micros() + SLEEP_CORRECTION_TIME, RF_NO_CAL);
}
