package com.ultreon.devices.datagen;

import com.ultreon.devices.init.DeviceItems;
import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.DyeColor;
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
        if (this.languageCode.startsWith("nl_")) {
            //createTranslationsForDutch(translationBuilder);
        }
    }

    private void createTranslationsForDutch(TranslationBuilder translationBuilder) { // TODO: @Qboi123
        DeviceItems.LAPTOPS.getMap().forEach((dye, item) -> {
            translationBuilder.add(item.get().getDescriptionId(), get(dye) + " Laptop");
        });

        DeviceItems.PRINTERS.getMap().forEach((dye, item) -> {
            translationBuilder.add(item.get().getDescriptionId(), get(dye) + " Printer");
        });

        DeviceItems.FLASH_DRIVE.getMap().forEach((dye, item) -> {
            translationBuilder.add(item.get().getDescriptionId(), get(dye) + " Flash Drive");
        });

        DeviceItems.ROUTERS.getMap().forEach((dye, item) -> {
            translationBuilder.add(item.get().getDescriptionId(), get(dye) + " Router");
        });

        DeviceItems.OFFICE_CHAIRS.getMap().forEach((dye, item) -> {
            translationBuilder.add(item.get().getDescriptionId(), get(dye) + " Office Chair");
        });
    }

    private void createTranslationsForEnglish(TranslationBuilder translationBuilder) {
        //System.out.println(dataGenerator.getModContainer().getRootPaths());
        try {
            translationBuilder.add(dataGenerator.getModContainer().findPath("en_us_existing.json").orElseThrow());
        } catch (Exception e) {e.printStackTrace();}
        DeviceItems.LAPTOPS.getMap().forEach((dye, item) -> {
            translationBuilder.add(item.get().getDescriptionId(), get(dye) + " Laptop");
        });

        DeviceItems.PRINTERS.getMap().forEach((dye, item) -> {
            translationBuilder.add(item.get().getDescriptionId(), get(dye) + " Printer");
        });

        DeviceItems.FLASH_DRIVE.getMap().forEach((dye, item) -> {
            translationBuilder.add(item.get().getDescriptionId(), get(dye) + " Flash Drive");
        });

        DeviceItems.ROUTERS.getMap().forEach((dye, item) -> {
            translationBuilder.add(item.get().getDescriptionId(), get(dye) + " Router");
        });

        DeviceItems.OFFICE_CHAIRS.getMap().forEach((dye, item) -> {
            translationBuilder.add(item.get().getDescriptionId(), get(dye) + " Office Chair");
        });
    }
    
    private String get(DyeColor dyeColor) {
        return I18n.get("color.minecraft." + dyeColor.getName());
    }
}
