package com.ultreon.devices.geo;

import com.ultreon.devices.Devices;
import com.ultreon.devices.item.MacMaxXItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class MacMaxXItemModel extends AnimatedGeoModel<MacMaxXItem> {
    @Override
    public ResourceLocation getModelResource(MacMaxXItem object) {
        return Devices.res("geo/mac_max_x.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MacMaxXItem object) {
        return Devices.res("textures/block/mac_max_x.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MacMaxXItem animatable) {
        return Devices.res("animations/mac_max_x.animation.json");
    }
}