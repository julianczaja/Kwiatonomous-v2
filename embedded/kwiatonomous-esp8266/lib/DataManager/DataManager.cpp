#include "DataManager.h"

DataManager::DataManager() {}

void DataManager::init() {}

void DataManager::setWiFiConfiguration(WiFiConfiguration *wifiConfiguration)
{
    EEPROM.begin(64);
    delay(10);
    EEPROM.put(WIFI_CONFIGURATION_ADDRESS, *wifiConfiguration);
    delay(10);
    EEPROM.end();
}

void DataManager::getWiFiConfiguration(WiFiConfiguration *wifiConfiguration)
{
    EEPROM.begin(64);
    delay(10);
    EEPROM.get(WIFI_CONFIGURATION_ADDRESS, *wifiConfiguration);
    EEPROM.end();
}

void DataManager::setDeviceConfiguration(DeviceConfiguration *deviceConfiguration)
{
    // Checking if the data isn't the same is built in 'EEPROM.put()' function
    EEPROM.begin(128);
    delay(10);
    EEPROM.put(DEVICE_CONFIGURATION_ADDRESS, *deviceConfiguration);
    delay(10);
    EEPROM.end();
}

void DataManager::getDeviceConfiguration(DeviceConfiguration *deviceConfiguration)
{
    EEPROM.begin(128);
    delay(10);
    EEPROM.get(DEVICE_CONFIGURATION_ADDRESS, *deviceConfiguration);
    EEPROM.end();
}

void DataManager::increaseFailuresCount()
{
    uint16_t failuresCount = 0;

    EEPROM.begin(128);
    delay(10);
    EEPROM.get(FAILURES_COUNT_ADDRESS, failuresCount);
    EEPROM.put(FAILURES_COUNT_ADDRESS, failuresCount + 1);
    delay(10);
    EEPROM.end();
}

uint16_t DataManager::getFailuresCount()
{
    uint16_t failuresCount = 0;

    EEPROM.begin(128);
    delay(10);
    EEPROM.get(FAILURES_COUNT_ADDRESS, failuresCount);
    EEPROM.end();

    return failuresCount;
}

void DataManager::clearAll()
{
    EEPROM.begin(2048);
    delay(10);
    for (int i = 0; i < 2048; ++i)
    {
        EEPROM.write(i, 0);
    }
    delay(10);
    EEPROM.end();
}
