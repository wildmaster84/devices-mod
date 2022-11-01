package com.ultreon.devices.block.entity.renderer.fabric;

import com.ultreon.devices.block.entity.MacMaxXBlockEntity;
import com.ultreon.devices.geo.MacMaxXModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class MacMaxXRenderer extends GeoBlockRenderer<MacMaxXBlockEntity> {
    public MacMaxXRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        super(new MacMaxXModel());
    }
}