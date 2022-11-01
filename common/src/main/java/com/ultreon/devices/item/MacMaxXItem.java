package com.ultreon.devices.item;

import com.ultreon.devices.ModDeviceTypes;
import net.minecraft.world.level.block.Block;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class MacMaxXItem extends DeviceItem implements IAnimatable {
    public MacMaxXItem(Block block, Properties properties) {
        super(block, properties, ModDeviceTypes.COMPUTER);
    }

    @Override
    public void registerControllers(AnimationData animationData) {

    }

    @Override
    public AnimationFactory getFactory() {
        return new AnimationFactory();
    }
}
