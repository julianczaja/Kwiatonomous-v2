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

#define LED_BUILTIN_PIN 2
#define PUMP_PIN 12
#define BLUE_LED_PIN 15
#define BATTERY_VOLTAGE_PIN A0
#define VOLTAGE_DIVIDER_PIN 14

// TODO: Hide in local file
#define WIFI_SSID "---"
#define WIFI_PASSWORD "---"
String serverName = "---";

#define SLEEP_TIME_NORMAL 5 * 60e6   // us
#define SLEEP_TIME_ERROR 2 * 60e6    // us
#define WIFI_CONNECTION_TIMEOUT 15e3 // ms
#define NTP_TIMEOUT 10e3             // ms

void lowBatteryCallback();
bool isAhtOn = false;
uint8_t batteryLevel;
float batteryVoltage;
int adcRaw;

WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org");
Adafruit_AHTX0 aht;
Pump pump = Pump(PUMP_PIN);
Led ledBuiltin = Led(LED_BUILTIN_PIN);
Led ledInfo = Led(BLUE_LED_PIN);
BatteryManager batteryManager = BatteryManager(BATTERY_VOLTAGE_PIN,
                                               VOLTAGE_DIVIDER_PIN,
                                               20,
                                               20,
                                               &lowBatteryCallback);

void setup()
{
  Serial.begin(115200);
  Serial.println("\nHello world!\n\n");

  analogWriteRange(1023);
  analogWriteResolution(10);

  pump.init();
  ledBuiltin.init(true);
  ledInfo.init(false);
  batteryManager.init();

  // Get battery status before Wi-Fi is on
  delay(2000);
  ledInfo.on(20);
  batteryManager.update();
  batteryLevel = batteryManager.getBatteryLevel();
  batteryVoltage = batteryManager.getBatteryVoltage();
  adcRaw = batteryManager.getLastRawValue();
  ledInfo.off();

  // Connect to Wi-Fi
  Serial.println("Connecting to Wi-Fi...");
  ledBuiltin.on(5);

  unsigned long *startConnection = (unsigned long *)malloc(sizeof(unsigned long));
  *startConnection = millis();
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  while (WiFi.status() != WL_CONNECTED)
  {
    if ((millis() - *startConnection) > WIFI_CONNECTION_TIMEOUT)
    {
      // If can't connect to Wi-Fi, sleep for 1min an try again
      ledInfo.signalizeWifiFailed();
      ESP.deepSleep(SLEEP_TIME_ERROR);
    }
    Serial.print(".");
    delay(100);
  }
  free(startConnection);
  Serial.print("\nConnected with IP: ");
  Serial.println(WiFi.localIP());

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

  ledBuiltin.off();
}

void loop()
{
  // Test pump
  pump.on();
  delay(1000);
  pump.off();

  // Test battery manager
  ledInfo.on(20);
  batteryManager.update();
  batteryLevel = batteryManager.getBatteryLevel();
  batteryVoltage = batteryManager.getBatteryVoltage();
  Serial.print("batteryLevel: ");
  Serial.println(batteryLevel);
  Serial.print("batteryVoltage: ");
  Serial.println(batteryVoltage);
  ledInfo.off();

  // Test AHT
  if (isAhtOn)
  {
    sensors_event_t humidity, temp;
    aht.getEvent(&humidity, &temp);
    Serial.print("humidity: ");
    Serial.println(humidity.relative_humidity);
    Serial.print("temperature: ");
    Serial.println(temp.temperature);
  }

  // Test time NTP
  timeClient.begin();
  unsigned long *startNtp = (unsigned long *)malloc(sizeof(unsigned long));
  *startNtp = millis();
  bool ntpSuccess = false;
  unsigned long epochTime;
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
  }
  timeClient.end();

  // Send data to server
  WiFiClient client;
  HTTPClient http;
  String serverPath = serverName + "/dupa/add";
  http.begin(client, serverPath.c_str());
  http.addHeader("Content-Type", "text/plain");
  // http.addHeader("Content-Type", "application/x-www-form-urlencoded");
  char payload[128];
  sprintf(payload, "Timestamp: %d\n Battery level: %d\n Battery voltage: %g", epochTime, batteryLevel, batteryVoltage);
  Serial.print("Sending payload: ");
  Serial.println(payload);
  int httpResponseCode = http.POST(payload);
  Serial.print("HTTP Response code: ");
  Serial.println(httpResponseCode);

  // Everything done, go to sleep
  Serial.println("Going to sleep...");
  ledInfo.off();
  ESP.deepSleep(30e6);
  // ESP.deepSleep(SLEEP_TIME_NORMAL);
}

void lowBatteryCallback()
{
  Serial.println("LOW BATTERY!!!");
  ledInfo.signalizeLowBattery();
}