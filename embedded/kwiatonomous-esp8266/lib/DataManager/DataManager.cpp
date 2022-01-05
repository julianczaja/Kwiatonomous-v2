#include "DataManager.h"

DataManager::DataManager() {}

void DataManager::init() {}

void DataManager::setWiFiConfiguration(WiFiConfiguration *wifiConfiguration)
{
    EEPROM.begin(64);
    delay(20);
    EEPROM.put(SSID_ADDRESS, wifiConfiguration->ssid);
    EEPROM.put(PASSWORD_ADDRESS, wifiConfiguration->password);
    EEPROM.end();
}

WiFiConfiguration DataManager::getWiFiConfiguration()
{
    WiFiConfiguration wifiConfiguration = WiFiConfiguration();

    EEPROM.begin(64);
    delay(20);
    for (int i = 0; i < 32; ++i)
    {
        wifiConfiguration.ssid[i] = EEPROM.read(SSID_ADDRESS + i);
        wifiConfiguration.password[i] = EEPROM.read(PASSWORD_ADDRESS + i);
    }
    EEPROM.end();

    return wifiConfiguration;
}

void DataManager::increaseFailuresCount()
{
    EEPROM.begin(128);
    uint16_t failuresCount = 0;
    EEPROM.get(FAILURES_COUNT_ADDRESS, failuresCount);
    EEPROM.put(FAILURES_COUNT_ADDRESS, failuresCount + 1);
    EEPROM.end();
}

void DataManager::clearAll()
{
    EEPROM.begin(1024);
    delay(20);
    for (int i = 0; i < 1024; ++i)
    {
        EEPROM.write(i, 0);
    }
    EEPROM.end();
}
