#include "DataManager.h"

DataManager::DataManager() {}

void DataManager::init() {}

void DataManager::setWiFiConfiguration(WiFiConfiguration *wifiConfiguration)
{
    EEPROM.begin(64);
    EEPROM.put(SSID_ADDRESS, wifiConfiguration->ssid);
    EEPROM.put(PASSWORD_ADDRESS, wifiConfiguration->password);
    EEPROM.end();
}

WiFiConfiguration DataManager::getWiFiConfiguration()
{
    WiFiConfiguration wifiConfiguration = WiFiConfiguration();

    EEPROM.begin(64);
    for (int i = 0; i < 32; ++i)
    {
        wifiConfiguration.ssid[i] = EEPROM.read(SSID_ADDRESS + i);
        wifiConfiguration.password[i] = EEPROM.read(PASSWORD_ADDRESS + i);
    }
    EEPROM.end();

    return wifiConfiguration;
}

void DataManager::clearAll()
{
    EEPROM.begin(1024);
    for (int i = 0; i < 1024; ++i)
    {
        EEPROM.write(i, 0);
    }
    EEPROM.end();
}
