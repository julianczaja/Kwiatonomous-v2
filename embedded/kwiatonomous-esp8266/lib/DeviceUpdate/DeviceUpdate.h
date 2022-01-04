#ifndef DEVICE_UPDATE_H
#define DEVICE_UPDATE_H

#include <Arduino.h>

struct DeviceUpdate
{
  unsigned long epochTime;
  int8_t batteryLevel;
  float batteryVoltage; 
  float temperature; 
  float humidity;
};

#endif