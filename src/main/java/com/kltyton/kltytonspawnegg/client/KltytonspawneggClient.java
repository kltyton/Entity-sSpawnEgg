package com.kltyton.kltytonspawnegg.client;

import com.kltyton.kltytonspawnegg.config.KltytonConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;

public class KltytonspawneggClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        AutoConfig.register(KltytonConfig.class, GsonConfigSerializer::new);
    }
}
