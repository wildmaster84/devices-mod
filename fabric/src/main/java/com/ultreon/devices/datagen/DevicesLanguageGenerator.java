package com.ultreon.devices.datagen;

import com.ultreon.devices.init.DeviceItems;
import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.Item;

import java.nio.file.Path;

public class DevicesLanguageGenerator extends FabricLanguageProvider {
    private final String languageCode;
    protected DevicesLanguageGenerator(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
        this.languageCode = "en_us";
    }

    protected DevicesLanguageGenerator(FabricDataGenerator dataGenerator, String languageCode) {
        super(dataGenerator, languageCode);
        this.languageCode = languageCode;
    }

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder) {
        if (this.languageCode.startsWith("en_")) {
            createTranslationsForEnglish(translationBuilder);
        }
    }

    private void createTranslationsForEnglish(TranslationBuilder translationBuilder) {
        try {
            translationBuilder.add(dataGenerator.getModContainer().findPath("assets/devices/lang/en_us_existing.json").orElseThrow());
        } catch (Exception e) {e.printStackTrace();}
        DeviceItems.LAPTOPS.getMap().forEach((dye, laptop) -> {
            translationBuilder.add(laptop.get().getDescriptionId(), I18n.get("color.minecraft." + dye.getName()) + " Laptop");
        });

        DeviceItems.PRINTERS.getMap().forEach((dye, laptop) -> {
            translationBuilder.add(laptop.get().getDescriptionId(), I18n.get("color.minecraft." + dye.getName()) + " Printer");
        });

        DeviceItems.FLASH_DRIVE.getMap().forEach((dye, laptop) -> {
            translationBuilder.add(laptop.get().getDescriptionId(), I18n.get("color.minecraft." + dye.getName()) + " Flash Drive");
        });

        DeviceItems.ROUTERS.getMap().forEach((dye, laptop) -> {
            translationBuilder.add(laptop.get().getDescriptionId(), I18n.get("color.minecraft." + dye.getName()) + " Router");
        });

        DeviceItems.OFFICE_CHAIRS.getMap().forEach((dye, laptop) -> {
            translationBuilder.add(laptop.get().getDescriptionId(), I18n.get("color.minecraft." + dye.getName()) + " Office Chair");
        });
    }
}
