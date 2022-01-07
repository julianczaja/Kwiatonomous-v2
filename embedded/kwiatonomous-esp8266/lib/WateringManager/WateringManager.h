#ifndef WATERING_MANAGER_H
#define WATERING_MANAGER_H

#include <Arduino.h>

#include "Pump.h"
#include "DeviceConfiguration.h"

class WateringManager
{
public:
    WateringManager(uint8_t pumpPin);

    void init();
    void update(DeviceConfiguration configuration, unsigned long currentEpochTime);
    bool isOn();

    unsigned long nextWatering = 4294967294; // max unsigned long value
    bool nextWateringUpdated = false;

private:
    void waterNow(int waterAmount);
    Pump _pump;
};

#endif