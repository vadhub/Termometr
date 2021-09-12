package com.vadim.termometr.utils;

public class TemperaturePaths {
    //all path that to get temperature from different device
    public static final String CPU_TEMP = "/sys/devices/system/cpu/cpu0/cpufreq/cpu_temp";
    public static final String FAKE_SHMOO_CPU_TEMP = "/sys/devices/system/cpu/cpu0/cpufreq/FakeShmoo_cpu_temp";
    public static final String TEMP = "/sys/class/thermal/thermal_zone1/temp";
    public static final String TEMPERATURE_I2C_4 = "/sys/class/i2c-adapter/i2c-4/4-004c/temperature";
    public static final String TEMPERATURE_I2C_3 = "/sys/devices/platform/tegra-i2c.3/i2c-4/4-004c/temperature";
    public static final String TEMPERATURE_OMAP = "/sys/devices/platform/omap/omap_temp_sensor.0/temperature";
    public static final String temp1_input = "/sys/devices/platform/tegra_tmon/temp1_input";
    public static final String TEMP_TJ = "/sys/kernel/debug/tegra_thermal/temp_tj";
    public static final String TEMPERATURE_S5P = "/sys/devices/platform/s5p-tmu/temperature";
    public static final String TEMP_CLASS_ZONE0 = "/sys/class/thermal/thermal_zone0/temp";
    public static final String TEMP_DEVICE_ZONE0 = "/sys/devices/virtual/thermal/thermal_zone0/temp";
    public static final String TEMP1_INPUT = "/sys/class/hwmon/hwmon0/device/temp1_input";
    public static final String TEMP_CLASS_ZONE1 = "/sys/devices/virtual/thermal/thermal_zone1/temp";
    public static final String CUURR_TEPM =  "/sys/devices/platform/s5p-tmu/curr_temp";
}
