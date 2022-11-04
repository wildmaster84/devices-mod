package com.ultreon.devices.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class DevicesDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        fabricDataGenerator.addProvider(DevicesModelGenerator::new);
        fabricDataGenerator.addProvider(DevicesLanguageGenerator::new);
        fabricDataGenerator.addProvider(new DevicesLanguageGenerator(fabricDataGenerator, "en_au"));
    }
}
