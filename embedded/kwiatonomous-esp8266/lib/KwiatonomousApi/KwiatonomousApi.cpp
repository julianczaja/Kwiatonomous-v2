#include "KwiatonomousApi.h"

KwiatonomousApi::KwiatonomousApi()
{
}

void KwiatonomousApi::init(const char *deviceId)
{
    _deviceId = deviceId;
    Serial.print("Initializing KwiatonomousApi for device with id: ");
    Serial.println(_deviceId);

    // Configure http client
    _http.setReuse(true);
    _http.setTimeout(5000);
}

bool KwiatonomousApi::getDeviceConfiguration(DeviceConfiguration *configuration)
{
    Serial.println("\n> KwiatonomousApi::getDeviceConfiguration");

    char path[128];
    sprintf(path, "%s/%s/configuration", SERVER_NAME, _deviceId);

    _http.begin(_client, path);
    _http.addHeader("Content-Type", "application/json");

    int httpGetResponseCode = _http.GET();
    if (httpGetResponseCode == HTTP_CODE_OK)
    {
        Serial.println("Success");
        String in_payload = _http.getString();
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

bool KwiatonomousApi::getNextWatering(unsigned long *nextWatering)
{
    Serial.println("\n> KwiatonomousApi::getNextWatering");

    char path[128];
    sprintf(path, "%s/%s/nextwatering", SERVER_NAME, _deviceId);

    _http.begin(_client, path);
    _http.addHeader("Content-Type", "application/json");

    int httpGetResponseCode = _http.GET();
    if (httpGetResponseCode == HTTP_CODE_OK)
    {
        Serial.println("Success");
        String in_payload = _http.getString();
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

bool KwiatonomousApi::updateNextWatering(unsigned long newNextWatering)
{
    Serial.println("\n> KwiatonomousApi::updateNextWatering");

    char path[128];
    sprintf(path, "%s/%s/nextwatering", SERVER_NAME, _deviceId);

    _http.begin(_client, path);
    _http.addHeader("Content-Type", "application/json");

    char payload[sizeof(newNextWatering)];
    sprintf(payload, "%lu", newNextWatering);
    Serial.print("Sending payload: ");
    Serial.println(payload);

    int httpResponseCode = _http.POST(payload);
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

bool KwiatonomousApi::sendUpdate(DeviceUpdate *deviceUpdate)
{
    Serial.println("\n> KwiatonomousApi::sendUpdate");

    char path[128];
    sprintf(path, "%s/%s/updates", SERVER_NAME, _deviceId);

    _http.begin(_client, path);
    _http.addHeader("Content-Type", "application/json");

    char payload[256];
    sprintf(payload, POST_UPDATE_FORMAT,
            deviceUpdate->epochTime,
            deviceUpdate->batteryLevel,
            deviceUpdate->batteryVoltage,
            deviceUpdate->temperature,
            deviceUpdate->humidity);
    Serial.print("Sending payload: ");
    Serial.println(payload);

    int httpResponseCode = _http.POST(payload);
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

void KwiatonomousApi::end()
{
    Serial.println("\n> KwiatonomousApi::end");

    _http.end();
}
