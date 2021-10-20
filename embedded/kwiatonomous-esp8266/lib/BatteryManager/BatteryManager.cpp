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
        delay(50);
    }

    voltageDividerOff();
    /*
        Battery level input
        1 V     -   1023
        0 V     -   0
        ------------------------------ ----------------------------------
        after voltage divider (1000 ohm and 330 ohm) (0-4.2 V to 0-1.0 V)
        -----------------------------------------------------------------
        1.000 V    -   1023    >   100% (4.03 V) 
        0.819 V    -   912     >   0%   (3.30 V)
    */
    float adcMean = (float)readSum / _samplesCount;
    _batteryLevel = map(adcMean, 912, 1023, 0, 100);
    _batteryVoltage = (adcMean / 1023.0) * ((1000.0 + 330.0) / 330.0);

    // 4.20 V --> 1.042 V --> 1023
    // 4.03 V --> 1.000 V --> 1023
    // 3.50 V --> 0.868 V --> 888
    // 3.30 V --> 0.819 V --> 838
    // float batteryVoltage = ((float)adcValue / 1024.0) * ((1000.0 + 330.0) / 330.0);

    // Serial.print("readSum: ");
    // Serial.println(readSum);
    // Serial.print("_samplesCount: ");
    // Serial.println(_samplesCount);
    // Serial.print("Battery level: ");
    // Serial.println(_batteryLevel);

    if (_batteryLevel < _lowBatteryLevel && (millis() - _lastCallbackTime) > 30000) // every 30s
    {
        lowBatteryCallback();
        _lastCallbackTime = millis();
    }
}

uint8_t BatteryManager::getBatteryLevel()
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