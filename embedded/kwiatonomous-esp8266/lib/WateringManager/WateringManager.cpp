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

    if (configuration.wateringOn == 0)
    {
        Serial.println("Watering is turned off");
        return;
    }

    if (currentEpochTime < nextWatering)
    {
        Serial.println("It's not time for watering yet");
        return;
    }

    unsigned long diff = currentEpochTime - nextWatering;
    unsigned long oneDaySeconds = 24 * 60 * 60;
    unsigned long wateringIntervalSeconds = configuration.wateringIntervalDays * oneDaySeconds;

    // What if device was turned off for a month?
    if (diff > (2 * wateringIntervalSeconds))
    {
        // We could just do: 'nextWatering = currentEpochTime + wateringIntervalSeconds',
        // but then the watering time could be different than the one set in configuration.
        int intervalsBack = diff / wateringIntervalSeconds;
        if (diff % wateringIntervalSeconds > 0)
        {
            intervalsBack++;
        }
        nextWatering = nextWatering + (wateringIntervalSeconds * intervalsBack);

        // Don't water now, wait until next planned watering time
        Serial.println("It's seems like your device was turned of for some time.");
        Serial.print("Let's wait with watering for ");
        Serial.print((nextWatering - currentEpochTime) / 60);
        Serial.println(" minutes");
    }
    // Normal case
    else
    {
        nextWatering = nextWatering + wateringIntervalSeconds;
        waterNow(configuration.wateringAmount);
    }

    Serial.print("New next watering time: ");
    Serial.println(nextWatering);

    nextWateringUpdated = true;
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
