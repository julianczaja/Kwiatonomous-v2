#ifndef BATTERY_MANAGER_H
#define BATTERY_MANAGER_H

#include <Arduino.h>

class BatteryManager
{
public:
    BatteryManager(uint8_t inPin, uint8_t dividerPin, uint8_t samplesCount, uint8_t lowBatteryLevel, void (*lbcb)());

    void init();
    void update();
    int8_t getBatteryLevel();
    float getBatteryVoltage();
    int getLastRawValue();

private:
    uint8_t _inPin;
    uint8_t _dividerPin;
    uint8_t _samplesCount;
    uint8_t _lowBatteryLevel;
    unsigned long _lastCallbackTime = 0;
    void (*lowBatteryCallback)();
    int8_t _batteryLevel = 0;
    float _batteryVoltage = 0;
    int _lastRawValue = 0;

    void voltageDividerOn();
    void voltageDividerOff();
    int8_t getBatteryLevelInternal(float adcMean);
};

#endif