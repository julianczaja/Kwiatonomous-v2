#ifndef KWIATONOMOUS_API_H
#define KWIATONOMOUS_API_H

#include <Arduino.h>
#include <ESP8266HTTPClient.h>
#include <WiFiClient.h>

#include "DeviceConfiguration.h"
#include "DeviceUpdate.h"

#define SERVER_NAME "---"
#define POST_UPDATE_FORMAT "{\"timestamp\":%lu,\"batteryLevel\":%d,\"batteryVoltage\":%g,\"temperature\":%g,\"humidity\":%g}"
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
    void end();

private:
    const char *_deviceId;

    HTTPClient _http;
    WiFiClient _client;
};

#endif