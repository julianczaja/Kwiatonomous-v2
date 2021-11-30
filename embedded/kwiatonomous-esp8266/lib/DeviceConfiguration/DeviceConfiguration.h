#ifndef DEVICE_CONFIGURATION_H
#define DEVICE_CONFIGURATION_H

struct DeviceConfiguration {
    int sleepTimeMinutes;
    int wateringOn;
    int wateringIntervalDays;
    int wateringAmount;
};

#endif