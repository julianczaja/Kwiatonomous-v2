#ifndef DATA_MANAGER_H
#define DATA_MANAGER_H

#include <Arduino.h>
#include <EEPROM.h>

#include "WiFiConfiguration.h"
#include "DeviceConfiguration.h"

#define WIFI_CONFIGURATION_ADDRESS 0
#define FAILURES_COUNT_ADDRESS 64
#define DEVICE_CONFIGURATION_ADDRESS 68
#define EEPROM_SIZE 128

#define OPERATIONS_DELAY_MS 30

class DataManager
{
public:
    DataManager();

    void init();
    
    void setWiFiConfiguration(WiFiConfiguration *wifiConfiguration);
    void getWiFiConfiguration(WiFiConfiguration *wifiConfiguration);
    
    void setDeviceConfiguration(DeviceConfiguration *deviceConfiguration);
    void getDeviceConfiguration(DeviceConfiguration *deviceConfiguration);

    void increaseFailuresCount();
    void resetFailuresCount();
    uint16_t getFailuresCount();

    void clearAll();

private:
    bool _isInitialized = false;
};

#endif