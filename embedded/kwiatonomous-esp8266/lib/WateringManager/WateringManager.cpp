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
    Serial.println("\n> WateringManager::update");

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
    Serial.println("\n> WateringManager::waterNow");

    uint16_t pumpingTime = 1000;

    // For now just define values available in application
    // TODO: Make formula to calculate delay for given water amount
    switch (waterAmount)
    {
    case 50:
        pumpingTime = 2500;
        break;
    case 100:
        pumpingTime = 5000;
        break;
    case 150:
        pumpingTime = 6500;
        break;
    case 250:
        pumpingTime = 10000;
        break;
    default:
        Serial.print("Unknown watering amount: ");
        Serial.println(waterAmount);
    }
    
    _pump.on();
    delay(pumpingTime);
    _pump.off();
}
