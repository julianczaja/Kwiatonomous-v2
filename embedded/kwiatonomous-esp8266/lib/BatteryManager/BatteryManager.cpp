#include "BatteryManager.h"

BatteryManager::BatteryManager(
    uint8_t inPin,
    uint8_t dividerPin,
    uint8_t samplesCount,
    uint8_t lowBatteryLevel,
    void (*lbcb)())
{
    _inPin = inPin;
    _dividerPin = dividerPin;
    _samplesCount = samplesCount;
    _lowBatteryLevel = lowBatteryLevel;
    lowBatteryCallback = lbcb;
}

void BatteryManager::init()
{
    analogWriteRange(1023);
    analogWriteResolution(10);
    pinMode(_inPin, INPUT);
    pinMode(_dividerPin, OUTPUT);
    update();
}

void BatteryManager::voltageDividerOn()
{
    digitalWrite(_dividerPin, HIGH);
}

void BatteryManager::voltageDividerOff()
{
    digitalWrite(_dividerPin, LOW);
}

void BatteryManager::update()
{
    voltageDividerOn();
    delay(50);

    uint16_t readSum = 0;
    for (int i = 0; i < _samplesCount; i++)
    {
        _lastRawValue = analogRead(_inPin);
        readSum += _lastRawValue;
        delay(20);
    }

    voltageDividerOff();

    /*  ---------------------------------------------
        ADC input range: 0-1V
        1 V     -   1023
        0 V     -   0
        ------------------------------ --------------
        Our battery voltage range: 2.65-4.20V
        ------------------------------ --------------
        after voltage divider (1000 ohm and 150 ohm) 
        ---------------------------------------------
        0.544 V    -   556   >   100% (4.20 V) 
        0.342 V    -   350   >   0%   (2.65 V)
        --------------------------------------------- */

    float adcMean = (float)readSum / (float)_samplesCount;
    _batteryLevel = (int8_t)((adcMean - 350.0) * 0.485436); // map(adcMean, 350, 556, 0, 100);
    _batteryVoltage = adcMean * 0.007494;                   // (adcMean / 1023.0) * ((1000.0 + 150.0) / 150.0);

    // Check for low battery
    if (_batteryLevel < _lowBatteryLevel && (millis() - _lastCallbackTime) > 30000) // every 30s
    {
        lowBatteryCallback();
        _lastCallbackTime = millis();
    }
}

int8_t BatteryManager::getBatteryLevel()
{
    return _batteryLevel;
}

float BatteryManager::getBatteryVoltage()
{
    return _batteryVoltage;
}

int BatteryManager::getLastRawValue()
{
    return _lastRawValue;
}