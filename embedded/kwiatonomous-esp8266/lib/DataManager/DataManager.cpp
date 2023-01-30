#include "DataManager.h"

DataManager::DataManager() {}

void DataManager::init() {}

void DataManager::setWiFiConfiguration(WiFiConfiguration *wifiConfiguration)
{
    Serial.println("\n> DataManager::setWiFiConfiguration");

    EEPROM.begin(EEPROM_SIZE);
    delay(OPERATIONS_DELAY_MS);
    EEPROM.put(WIFI_CONFIGURATION_ADDRESS, *wifiConfiguration);
    delay(OPERATIONS_DELAY_MS);
    EEPROM.end();
}

void DataManager::getWiFiConfiguration(WiFiConfiguration *wifiConfiguration)
{
    Serial.println("\n> DataManager::getWiFiConfiguration");

    EEPROM.begin(EEPROM_SIZE);
    delay(OPERATIONS_DELAY_MS);
    EEPROM.get(WIFI_CONFIGURATION_ADDRESS, *wifiConfiguration);
    EEPROM.end();
}

void DataManager::setDeviceConfiguration(DeviceConfiguration *deviceConfiguration)
{
    Serial.println("\n> DataManager::setDeviceConfiguration");

    // Checking if the data isn't the same is built in 'EEPROM.put()' function
    EEPROM.begin(EEPROM_SIZE);
    delay(OPERATIONS_DELAY_MS);
    EEPROM.put(DEVICE_CONFIGURATION_ADDRESS, *deviceConfiguration);
    delay(OPERATIONS_DELAY_MS);
    EEPROM.end();
}

void DataManager::getDeviceConfiguration(DeviceConfiguration *deviceConfiguration)
{
    Serial.println("\n> DataManager::getDeviceConfiguration");

    EEPROM.begin(EEPROM_SIZE);
    delay(OPERATIONS_DELAY_MS);
    EEPROM.get(DEVICE_CONFIGURATION_ADDRESS, *deviceConfiguration);
    EEPROM.end();
}

void DataManager::increaseFailuresCount()
{
    Serial.println("\n> DataManager::increaseFailuresCount");

    uint16_t failuresCount = 0;

    EEPROM.begin(EEPROM_SIZE);
    delay(OPERATIONS_DELAY_MS);
    EEPROM.get(FAILURES_COUNT_ADDRESS, failuresCount);
    delay(OPERATIONS_DELAY_MS);
    EEPROM.put(FAILURES_COUNT_ADDRESS, (uint16_t)(failuresCount + 1));
    delay(OPERATIONS_DELAY_MS);
    EEPROM.end();
}

uint16_t DataManager::getFailuresCount()
{
    Serial.println("\n> DataManager::getFailuresCount");

    uint16_t failuresCount = 0;

    EEPROM.begin(EEPROM_SIZE);
    delay(OPERATIONS_DELAY_MS);
    EEPROM.get(FAILURES_COUNT_ADDRESS, failuresCount);
    EEPROM.end();

    return failuresCount;
}

void DataManager::resetFailuresCount()
{
    Serial.println("\n> DataManager::resetFailuresCount");

    EEPROM.begin(EEPROM_SIZE);
    delay(OPERATIONS_DELAY_MS);
    EEPROM.put(FAILURES_COUNT_ADDRESS, 0);
    EEPROM.end();
}

void DataManager::clearAll()
{
    Serial.println("\n> DataManager::clearAll");

    EEPROM.begin(EEPROM_SIZE);
    delay(OPERATIONS_DELAY_MS);
    for (int i = 0; i < EEPROM_SIZE; ++i)
    {
        EEPROM.write(i, 0);
    }
    delay(OPERATIONS_DELAY_MS);
    EEPROM.end();
}
