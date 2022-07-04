package com.mrcrayfish.device.init;

import com.mrcrayfish.device.Reference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Author: MrCrayfish
 */
public class DeviceSounds {
    private static final DeferredRegister<SoundEvent> REGISTER = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Reference.MOD_ID);

    public static final RegistryObject<SoundEvent> PRINTER_PRINTING = REGISTER.register("printer_printing", () -> new SoundEvent(new ResourceLocation(Reference.MOD_ID, "printer_printing")));
    public static final RegistryObject<SoundEvent> PRINTER_LOADING_PAPER = REGISTER.register("printer_loading_paper", () -> new SoundEvent(new ResourceLocation(Reference.MOD_ID, "printer_loading_paper")));

//    static {
//        PRINTER_PRINTING = registerSound("cdm:printing_ink");
//        PRINTER_LOADING_PAPER = registerSound("cdm:printing_paper");
//    }
//
//    private static SoundEvent registerSound(String soundNameIn) {
//        ResourceLocation resource = new ResourceLocation(soundNameIn);
//        return new SoundEvent(resource).setRegistryName(soundNameIn);
//    }

    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }

//    @Mod.EventBusSubscriber(modid = Reference.MOD_ID)
//    public static class RegistrationHandler {
//        @SubscribeEvent
//        public static void registerSounds(final RegistryEvent.Register<SoundEvent> event) {
//            event.getRegistry().register(PRINTER_PRINTING);
//            event.getRegistry().register(PRINTER_LOADING_PAPER);
//        }
//    }
}
