#include "WateringManager.h"

WateringManager::WateringManager(uint8_t pumpPin)
{
    _pump = Pump(pumpPin);
}

void WateringManager::init()
{
    _pump.init();
}

void WateringManager::update(DeviceConfiguration configuration, unsigned long currentEpochTime)
{
    if (configuration.wateringOn == 1)
    {
        on();
        if (currentEpochTime >= nextWatering)
        {
            waterNow(configuration.wateringAmount);
            nextWatering = nextWatering + (configuration.wateringIntervalDays * 24 * 60 * 60);
            Serial.print("New next watering time: ");
            Serial.println(nextWatering);

            nextWateringUpdated = true;
        }
        else
        {
            Serial.println("It's not time for watering yet");
        }
    }
    else
    {
        off();
    }
}

void WateringManager::on()
{
    _isOn = true;
}

void WateringManager::off()
{
    _isOn = false;
}

bool WateringManager::isOn()
{
    return _isOn;
}

void WateringManager::waterNow(int waterAmount)
{
    Serial.println("waterNow");
    // TODO: Make formula to calculate delay for given water amount
    _pump.on();
    delay(1000);
    _pump.off();
}