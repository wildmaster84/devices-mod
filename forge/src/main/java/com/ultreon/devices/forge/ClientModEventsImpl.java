package com.ultreon.devices.forge;

import com.ultreon.devices.block.entity.renderer.forge.MacMaxXRenderer;
import com.ultreon.devices.init.DeviceBlockEntities;
import com.ultreon.devices.object.AppInfo;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

public class ClientModEventsImpl {
    public static void setRenderLayer(Block block, RenderType renderType) {
        ItemBlockRenderTypes.setRenderLayer(block, renderType);
    }

    public static void updateIcon(AppInfo info, int iconU, int iconV) {
        ObfuscationReflectionHelper.setPrivateValue(AppInfo.class, info, iconU, "iconU");
        ObfuscationReflectionHelper.setPrivateValue(AppInfo.class, info, iconV, "iconV");
    }

    public static void registerGeoRenderers() {
        BlockEntityRendererRegistry.register(DeviceBlockEntities.MAC_MAX_X.get(), MacMaxXRenderer::new);
    }
}
