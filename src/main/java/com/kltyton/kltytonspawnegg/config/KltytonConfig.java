package com.kltyton.kltytonspawnegg.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "kltyton_spawn_egg_config")
public class KltytonConfig implements ConfigData {
    public float totalZoomRate = 1.0f;
    public float iconZoomRate = 1.0f;
    public float rotationAngle = -45.0f;
    public float itemZoomRate = 1.0f;
    public float maxZoomRate = 0.38f;
    public float minZoomRate = 0.1f;
}

