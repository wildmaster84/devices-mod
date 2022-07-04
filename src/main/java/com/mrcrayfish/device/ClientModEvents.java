package com.mrcrayfish.device;

import com.mrcrayfish.device.block.entity.renderer.LaptopRenderer;
import com.mrcrayfish.device.block.entity.renderer.PaperRenderer;
import com.mrcrayfish.device.block.entity.renderer.PrinterRenderer;
import com.mrcrayfish.device.block.entity.renderer.RouterRenderer;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.init.DeviceBlockEntities;
import com.mrcrayfish.device.init.DeviceBlocks;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    public static final Logger LOGGER = LoggerFactory.getLogger("CDM-ClientModEvents");

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        LOGGER.info("Doing some client setup.");

        if (MrCrayfishDeviceMod.DEVELOPER_MODE) {
            LOGGER.info("Adding developer wallpaper.");
            Laptop.addWallpaper(new ResourceLocation("cdm:textures/gui/developer_wallpaper.png"));
        } else {
            LOGGER.info("Adding default wallpapers.");
            Laptop.addWallpaper(new ResourceLocation("cdm:textures/gui/laptop_wallpaper_1.png"));
            Laptop.addWallpaper(new ResourceLocation("cdm:textures/gui/laptop_wallpaper_2.png"));
            Laptop.addWallpaper(new ResourceLocation("cdm:textures/gui/laptop_wallpaper_3.png"));
            Laptop.addWallpaper(new ResourceLocation("cdm:textures/gui/laptop_wallpaper_4.png"));
            Laptop.addWallpaper(new ResourceLocation("cdm:textures/gui/laptop_wallpaper_5.png"));
            Laptop.addWallpaper(new ResourceLocation("cdm:textures/gui/laptop_wallpaper_6.png"));
            Laptop.addWallpaper(new ResourceLocation("cdm:textures/gui/laptop_wallpaper_7.png"));
        }

        DeviceBlocks.getLaptops().forEach(block -> {
            LOGGER.debug("Setting render layer for laptop {}", block.getRegistryName());
            ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutout());
        });
        DeviceBlocks.getPrinters().forEach(block -> {
            LOGGER.debug("Setting render layer for printer {}", block.getRegistryName());
            ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutout());
        });
        DeviceBlocks.getRouters().forEach(block -> {
            LOGGER.debug("Setting render layer for router {}", block.getRegistryName());
            ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutout());
        });
        LOGGER.debug("Setting render layer for paper {}", DeviceBlocks.PAPER.get().getRegistryName());
        ItemBlockRenderTypes.setRenderLayer(DeviceBlocks.PAPER.get(), RenderType.cutout());

    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        LOGGER.info("Registering renderers.");

        event.registerBlockEntityRenderer(DeviceBlockEntities.LAPTOP.get(), LaptopRenderer::new);
        event.registerBlockEntityRenderer(DeviceBlockEntities.PRINTER.get(), PrinterRenderer::new);
        event.registerBlockEntityRenderer(DeviceBlockEntities.PAPER.get(), PaperRenderer::new);
        event.registerBlockEntityRenderer(DeviceBlockEntities.ROUTER.get(), RouterRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        LOGGER.info("Registering layer definitions.");
        event.registerLayerDefinition(PrinterRenderer.PaperModel.LAYER_LOCATION, PrinterRenderer.PaperModel::createBodyLayer);
    }
}
