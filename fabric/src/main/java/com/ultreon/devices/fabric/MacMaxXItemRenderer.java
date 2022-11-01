package com.ultreon.devices.fabric;

import com.ultreon.devices.geo.MacMaxXItemModel;
import com.ultreon.devices.item.MacMaxXItem;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class MacMaxXItemRenderer extends GeoItemRenderer<MacMaxXItem> {
    public MacMaxXItemRenderer() {
        super(new MacMaxXItemModel());
    }
}
