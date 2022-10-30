package com.ultreon.devices.block.entity.forge;

import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.ApiStatus;

import static net.minecraftforge.common.extensions.IForgeBlockEntity.INFINITE_EXTENT_AABB;

@ApiStatus.Internal
public class RouterBlockEntityImpl {
    public AABB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }
}
