#ifndef PUMP_H
#define PUMP_H

#include <Arduino.h>

class Pump
{
public:
    Pump(uint8_t pumpPin = 12);

    void init();
    void on();
    void off();
    bool isOn();

private:
    uint8_t _pumpPin;
    bool _isOn;
};

#endif