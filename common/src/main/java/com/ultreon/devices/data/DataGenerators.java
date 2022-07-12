//package com.ultreon.devices.data;
//
//import com.ultreon.devices.Reference;
//import com.ultreon.devices.data.client.ModBlockStateProvider;
//import com.ultreon.devices.data.client.ModItemModelProvider;
//import com.ultreon.devices.data.loot.ModLootTableProvider;
//import com.ultreon.devices.data.tags.ModBlockTagsProvider;
//import com.ultreon.devices.data.tags.ModItemTagsProvider;
//import net.minecraft.data.DataGenerator;
//
//@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
//public final class DataGenerators {
//    private DataGenerators() {
//        throw new UnsupportedOperationException("Can't instantiate utility class");
//    }
//
//    @SubscribeEvent
//    public static void gatherData(GatherDataEvent event) {
//        DataGenerator gen = event.getGenerator();
//        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
//
//        gen.addProvider(new ModBlockStateProvider(gen, existingFileHelper));
//        gen.addProvider(new ModItemModelProvider(gen, existingFileHelper));
//        gen.addProvider(new ModRecipesProvider(gen));
//        gen.addProvider(new ModLootTableProvider(gen));
//
//        ModBlockTagsProvider blockTags = new ModBlockTagsProvider(gen, existingFileHelper);
//        gen.addProvider(blockTags);
//        gen.addProvider(new ModItemTagsProvider(gen, blockTags, existingFileHelper));
//    }
//}
