package com.ultreon.devices;

import com.ultreon.devices.block.entity.renderer.LaptopRenderer;
import com.ultreon.devices.block.entity.renderer.PaperRenderer;
import com.ultreon.devices.block.entity.renderer.PrinterRenderer;
import com.ultreon.devices.block.entity.renderer.RouterRenderer;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.init.DeviceBlockEntities;
import com.ultreon.devices.init.DeviceBlocks;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import dev.architectury.registry.registries.Registries;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;


public class ClientModEvents {
    private static final Marker SETUP = MarkerFactory.getMarker("SETUP");
    private static final Logger LOGGER = Devices.LOGGER;

    public static void clientSetup() {
        LOGGER.info("Doing some client setup.");

        if (Devices.DEVELOPER_MODE) {
            LOGGER.info(SETUP, "Adding developer wallpaper.");
            Laptop.addWallpaper(new ResourceLocation("devices:textures/gui/developer_wallpaper.png"));
        } else {
            LOGGER.info(SETUP, "Adding default wallpapers.");
            Laptop.addWallpaper(new ResourceLocation("devices:textures/gui/laptop_wallpaper_1.png"));
            Laptop.addWallpaper(new ResourceLocation("devices:textures/gui/laptop_wallpaper_2.png"));
            Laptop.addWallpaper(new ResourceLocation("devices:textures/gui/laptop_wallpaper_3.png"));
            Laptop.addWallpaper(new ResourceLocation("devices:textures/gui/laptop_wallpaper_4.png"));
            Laptop.addWallpaper(new ResourceLocation("devices:textures/gui/laptop_wallpaper_5.png"));
            Laptop.addWallpaper(new ResourceLocation("devices:textures/gui/laptop_wallpaper_6.png"));
            Laptop.addWallpaper(new ResourceLocation("devices:textures/gui/laptop_wallpaper_7.png"));
        }


        // Register other stuff.
        registerRenderLayers();
        registerRenderers();
        registerLayerDefinitions();
    }

    private static void registerRenderLayers() {
        DeviceBlocks.getAllLaptops().forEach(block -> {
            LOGGER.debug(SETUP, "Setting render layer for laptop {}", Registries.getId(block, Registry.BLOCK_REGISTRY));
            RenderTypeRegistry.register(RenderType.cutout(), block);
        });

        DeviceBlocks.getAllPrinters().forEach(block -> {
            LOGGER.debug(SETUP, "Setting render layer for printer {}", Registries.getId(block, Registry.BLOCK_REGISTRY));
            RenderTypeRegistry.register(RenderType.cutout(), block);
        });

        DeviceBlocks.getAllRouters().forEach(block -> {
            LOGGER.debug(SETUP, "Setting render layer for router {}", Registries.getId(block, Registry.BLOCK_REGISTRY));
            RenderTypeRegistry.register(RenderType.cutout(), block);
        });

        LOGGER.debug(SETUP, "Setting render layer for paper {}", Registries.getId(DeviceBlocks.PAPER.get(), Registry.BLOCK_REGISTRY));
        RenderTypeRegistry.register(RenderType.cutout(), DeviceBlocks.PAPER.get());
    }

    @ExpectPlatform
    public static void setRenderLayer(Block block, RenderType type) {
        throw new AssertionError();
    }

    public static void registerRenderers() {
        LOGGER.info("Registering renderers.");

        BlockEntityRendererRegistry.register(DeviceBlockEntities.LAPTOP.get(), LaptopRenderer::new);
        BlockEntityRendererRegistry.register(DeviceBlockEntities.PRINTER.get(), PrinterRenderer::new);
        BlockEntityRendererRegistry.register(DeviceBlockEntities.PAPER.get(), PaperRenderer::new);
        BlockEntityRendererRegistry.register(DeviceBlockEntities.ROUTER.get(), RouterRenderer::new);
    }

    public static void registerLayerDefinitions() {
        LOGGER.info("Registering layer definitions.");
        EntityModelLayerRegistry.register(PrinterRenderer.PaperModel.LAYER_LOCATION, PrinterRenderer.PaperModel::createBodyLayer);
    }
}
