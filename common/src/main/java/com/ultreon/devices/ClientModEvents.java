package com.ultreon.devices;

import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.init.DeviceBlocks;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.registry.registries.Registries;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ClientModEvents {
    public static final Logger LOGGER = LoggerFactory.getLogger("CDM-ClientModEvents");

    public static void clientSetup() {
        LOGGER.info("Doing some client setup.");

        if (Devices.DEVELOPER_MODE) {
            LOGGER.info("Adding developer wallpaper.");
            Laptop.addWallpaper(new ResourceLocation("devices:textures/gui/developer_wallpaper.png"));
        } else {
            LOGGER.info("Adding default wallpapers.");
            Laptop.addWallpaper(new ResourceLocation("devices:textures/gui/laptop_wallpaper_1.png"));
            Laptop.addWallpaper(new ResourceLocation("devices:textures/gui/laptop_wallpaper_2.png"));
            Laptop.addWallpaper(new ResourceLocation("devices:textures/gui/laptop_wallpaper_3.png"));
            Laptop.addWallpaper(new ResourceLocation("devices:textures/gui/laptop_wallpaper_4.png"));
            Laptop.addWallpaper(new ResourceLocation("devices:textures/gui/laptop_wallpaper_5.png"));
            Laptop.addWallpaper(new ResourceLocation("devices:textures/gui/laptop_wallpaper_6.png"));
            Laptop.addWallpaper(new ResourceLocation("devices:textures/gui/laptop_wallpaper_7.png"));
        }

        DeviceBlocks.getAllLaptops().forEach(block -> {
            LOGGER.debug("Setting render layer for laptop {}", Registries.getId(block, Registry.BLOCK_REGISTRY));
            setRenderLayer(block, RenderType.cutout());
        });
        DeviceBlocks.getAllPrinters().forEach(block -> {
            LOGGER.debug("Setting render layer for printer {}", Registries.getId(block, Registry.BLOCK_REGISTRY));
            setRenderLayer(block, RenderType.cutout());
        });
        DeviceBlocks.getAllRouters().forEach(block -> {
            LOGGER.debug("Setting render layer for router {}", Registries.getId(block, Registry.BLOCK_REGISTRY));
            setRenderLayer(block, RenderType.cutout());
        });
        LOGGER.debug("Setting render layer for paper {}", Registries.getId(DeviceBlocks.PAPER.get(), Registry.BLOCK_REGISTRY));
        setRenderLayer(DeviceBlocks.PAPER.get(), RenderType.cutout());

    }
    
    @ExpectPlatform
    public static void setRenderLayer(Block block, RenderType type) {
        throw new AssertionError();
    }

    //TODO
//    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
//        LOGGER.info("Registering renderers.");
//
//        event.registerBlockEntityRenderer(DeviceBlockEntities.LAPTOP.get(), LaptopRenderer::new);
//        event.registerBlockEntityRenderer(DeviceBlockEntities.PRINTER.get(), PrinterRenderer::new);
//        event.registerBlockEntityRenderer(DeviceBlockEntities.PAPER.get(), PaperRenderer::new);
//        event.registerBlockEntityRenderer(DeviceBlockEntities.ROUTER.get(), RouterRenderer::new);
//    }
//
//    @SubscribeEvent
//    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
//        LOGGER.info("Registering layer definitions.");
//        event.registerLayerDefinition(PrinterRenderer.PaperModel.LAYER_LOCATION, PrinterRenderer.PaperModel::createBodyLayer);
//    }
}
