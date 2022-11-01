package com.ultreon.devices.block.entity;

import com.ultreon.devices.init.DeviceBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class MacMaxXBlockEntity extends ComputerBlockEntity implements IAnimatable {
    public MacMaxXBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(DeviceBlockEntities.MAC_MAX_X.get(), pWorldPosition, pBlockState);
    }

    @Override
    public void registerControllers(AnimationData animationData) {

    }

    @Override
    public AnimationFactory getFactory() {
        return new AnimationFactory(this);
    }
}
