#include <Arduino.h>
#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <WiFiClient.h>
#include <WiFiUdp.h>
#include <NTPClient.h>
#include <Adafruit_AHTX0.h>

#include "Pump.h"
#include "Led.h"
#include "BatteryManager.h"

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
#define WIFI_SSID "---"
#define WIFI_PASSWORD "---"
#define SERVER_NAME "---"
#define POST_FORMAT "{\"timestamp\":%d,\"batteryLevel\":%d,\"batteryVoltage\":%g,\"temperature\":%g,\"humidity\":%g}"

void lowBatteryCallback();
void goToSleep(unsigned long sleepTime);
void connectToWifi();
unsigned long getEpochTime();

bool isAhtOn = false;
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org");
Adafruit_AHTX0 aht;
Pump pump = Pump(PUMP_PIN);
Led ledBuiltin = Led(LED_BUILTIN_PIN);
Led ledInfo = Led(LED_BLUE_PIN);
BatteryManager batteryManager = BatteryManager(BATTERY_VOLTAGE_PIN,
                                               VOLTAGE_DIVIDER_PIN,
                                               25,
                                               20,
                                               &lowBatteryCallback);

void setup()
{
  Serial.begin(115200);
  Serial.println("\nHello world!\n\n");

  pump.init();
  ledBuiltin.init(true);
  ledInfo.init(false);
  batteryManager.init();
  delay(250);

  // Get battery status before Wi-Fi is on
  ledInfo.on(20);
  batteryManager.update();
  ledInfo.off();

  // Connect to Wi-Fi
  connectToWifi();

  // Initialize AHT sensor
  if (!aht.begin())
  {
    Serial.println("Could not find AHT :( Check wiring!");
  }
  else
  {
    Serial.println("AHT found");
    isAhtOn = true;
  }
}

void loop()
{
  ledInfo.on(10);

  // Test pump
  pump.on();
  delay(1000);
  pump.off();

  // Test AHT
  float temperature = 0.0f;
  float humidity = 0.0f;
  if (isAhtOn)
  {
    sensors_event_t sensor_temperature, sensor_humidity;
    aht.getEvent(&sensor_humidity, &sensor_temperature);
    temperature = sensor_temperature.temperature;
    humidity = sensor_humidity.relative_humidity;
  }

  // Get current time
  unsigned long epochTime = getEpochTime();

  // Send data to server
  WiFiClient client;
  HTTPClient http;

  char path[128];
  sprintf(path, "%s/%s/updates", SERVER_NAME, DEVICE_ID);

  http.setReuse(false);
  http.begin(client, path);
  http.addHeader("Content-Type", "application/json");

  char payload[256];
  sprintf(payload, POST_FORMAT, epochTime, batteryManager.getBatteryLevel(),
          batteryManager.getBatteryVoltage(), temperature, humidity);
  Serial.print("Sending payload: ");
  Serial.println(payload);

  int httpResponseCode = http.POST(payload);
  Serial.print("HTTP Response code: ");
  Serial.println(httpResponseCode);

  http.end();
  delay(500);

  // Everything done, go to sleep
  Serial.println("Going to sleep...");
  ledInfo.off();
  goToSleep(SLEEP_TIME_NORMAL);
}

void connectToWifi()
{
  Serial.println("Connecting to Wi-Fi...");
  ledBuiltin.on(5);

  unsigned long *startConnection = (unsigned long *)malloc(sizeof(unsigned long));
  *startConnection = millis();

  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);

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