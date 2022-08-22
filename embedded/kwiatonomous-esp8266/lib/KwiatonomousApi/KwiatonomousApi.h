#ifndef KWIATONOMOUS_API_H
#define KWIATONOMOUS_API_H

#include <Arduino.h>
#include <ESP8266HTTPClient.h>
#include <WiFiClient.h>

#include "DeviceConfiguration.h"
#include "DeviceUpdate.h"

#define SERVER_NAME "http://maluch.mikr.us:20188/kwiatonomous/esp"
// #define SERVER_NAME "http://192.168.1.11:8015/kwiatonomous/esp" // FOR LOCAL TESTS
#define POST_UPDATE_FORMAT "{\"timestamp\":%lu,\"batteryLevel\":%d,\"batteryVoltage\":%g,\"temperature\":%g,\"humidity\":%g}"
#define POST_WATERING_EVENT_FORMAT "{\"timestamp\":%lu,\"type\":\"Watering\",\"data\":\"\"}"
#define GET_CONFIGURATION_FORMAT "{\"sleepTimeMinutes\":%d,\"timeZoneOffset\":%d,\"wateringOn\":%d,\"wateringIntervalDays\":%d,\"wateringAmount\":%d,\"wateringTime\":%s}"
class KwiatonomousApi
{
public:
    KwiatonomousApi();

    void init(const char *deviceId);
    bool getDeviceConfiguration(DeviceConfiguration *configuration);
    bool getNextWatering(unsigned long *nextWatering);
    bool updateNextWatering(unsigned long newNextWatering);
    bool sendUpdate(DeviceUpdate *deviceUpdate);
    bool sendWateringEvent(unsigned long timestamp);
    void end();

private:
    const char *_deviceId;

    HTTPClient _http;
    WiFiClient _client;
};

#endif