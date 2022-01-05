#ifndef DATA_MANAGER_H
#define DATA_MANAGER_H

#include <Arduino.h>
#include <EEPROM.h>

#include "WiFiConfiguration.h"

#define SSID_ADDRESS 0
#define PASSWORD_ADDRESS 32
#define FAILURES_COUNT_ADDRESS 64

class DataManager
{
public:
    DataManager();

    void init();
    void setWiFiConfiguration(WiFiConfiguration *wifiConfiguration);
    WiFiConfiguration getWiFiConfiguration();
    void increaseFailuresCount();
    void clearAll();

private:
    bool _isInitialized = false;
};

#endif