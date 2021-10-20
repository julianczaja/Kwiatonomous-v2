#include "Pump.h"

Pump::Pump(uint8_t pumpPin)
{
    _pumpPin = pumpPin;
}

void Pump::init()
{
    pinMode(_pumpPin, OUTPUT);
    off();
}

void Pump::on()
{
    digitalWrite(_pumpPin, HIGH);
    _isOn = true;
}

void Pump::off()
{
    digitalWrite(_pumpPin, LOW);
    _isOn = false;
}

bool Pump::isOn()
{
    return _isOn;
}
