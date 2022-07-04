package com.mrcrayfish.device.block.entity;

import com.mrcrayfish.device.core.network.Router;
import com.mrcrayfish.device.init.DeviceBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings("unused")
public class RouterBlockEntity extends DeviceBlockEntity.Colored {
    private Router router;

    @OnlyIn(Dist.CLIENT)
    private int debugTimer;

    public RouterBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(DeviceBlockEntities.ROUTER.get(), pWorldPosition, pBlockState);
    }

    public Router getRouter() {
        if (router == null) {
            router = new Router(worldPosition);
            setChanged();
        }
        return router;
    }

    public void tick() {
        assert level != null;
        if (!level.isClientSide) {
            getRouter().tick(level);
        } else if (debugTimer > 0) {
            debugTimer--;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isDebug() {
        return debugTimer > 0;
    }

    @OnlyIn(Dist.CLIENT)
    public void setDebug(boolean debug) {
        if (debug) {
            debugTimer = 1200;
        } else {
            debugTimer = 0;
        }
    }

    public String getDeviceName() {
        return "Router";
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (tag.contains("router", Tag.TAG_COMPOUND)) {
            router = Router.fromTag(worldPosition, tag.getCompound("router"));
        }
    }

    public void syncDevicesToClient() {
        pipeline.put("router", getRouter().toTag(true));
        sync();
    }

    // Todo - Maybe implement this whenever possible?
//    @Override
//    public double getMaxRenderDistanceSqr() {
//        return 16384;
//    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public AABB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }
}
