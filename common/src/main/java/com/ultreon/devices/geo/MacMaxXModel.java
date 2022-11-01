package com.ultreon.devices.geo;

import com.ultreon.devices.Devices;
import com.ultreon.devices.block.entity.MacMaxXBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class MacMaxXModel extends AnimatedGeoModel<MacMaxXBlockEntity> {
    @Override
    public ResourceLocation getModelResource(MacMaxXBlockEntity object) {
        return Devices.res("geo/mac_max_x.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MacMaxXBlockEntity object) {
        return Devices.res("textures/block/mac_max_x.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MacMaxXBlockEntity animatable) {
        return Devices.res("animations/mac_max_x.animation.json");
    }
}