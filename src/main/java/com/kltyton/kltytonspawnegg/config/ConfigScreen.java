package com.kltyton.kltytonspawnegg.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ConfigScreen {

    public static Screen getConfigScreen(Screen parent) {
        // 创建配置界面构建器(未启用)
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.of("KltytonSpawnEgg"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        KltytonConfig config = AutoConfig.getConfigHolder(KltytonConfig.class).getConfig();
        builder.getOrCreateCategory(Text.of("通用"))
                .addEntry(entryBuilder.startFloatField(Text.of("总缩放率"), config.totalZoomRate)
                        .setDefaultValue(1.0f)
                        .setSaveConsumer(newValue -> config.totalZoomRate = newValue)
                        .setTooltip(Text.of("设置总缩放比例"))
                        .build())
                .addEntry(entryBuilder.startFloatField(Text.of("图标缩放率"), config.iconZoomRate)
                        .setDefaultValue(1.0f)
                        .setSaveConsumer(newValue -> config.iconZoomRate = newValue)
                        .setTooltip(Text.of("设置图标缩放比例"))
                        .build())
                .addEntry(entryBuilder.startFloatField(Text.of("旋转角度"), config.rotationAngle)
                        .setDefaultValue(-45.0f)
                        .setSaveConsumer(newValue -> config.rotationAngle = newValue)
                        .setTooltip(Text.of("设置旋转角度"))
                        .build())
                .addEntry(entryBuilder.startFloatField(Text.of("物品缩放率"), config.itemZoomRate)
                        .setDefaultValue(1.0f)
                        .setSaveConsumer(newValue -> config.itemZoomRate = newValue)
                        .setTooltip(Text.of("设置物品缩放比例"))
                        .build())
                .addEntry(entryBuilder.startFloatField(Text.of("最大缩放率"), config.maxZoomRate)
                        .setDefaultValue(0.38f)
                        .setSaveConsumer(newValue -> config.maxZoomRate = newValue)
                        .setTooltip(Text.of("设置最大缩放比例"))
                        .build())
                .addEntry(entryBuilder.startFloatField(Text.of("最小缩放率"), config.minZoomRate)
                        .setDefaultValue(0.1f)
                        .setSaveConsumer(newValue -> config.minZoomRate = newValue)
                        .setTooltip(Text.of("设置最小缩放比例"))
                        .build());

        return builder.build();
    }
}