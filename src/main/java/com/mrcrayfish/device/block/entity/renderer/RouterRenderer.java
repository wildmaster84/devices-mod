package com.mrcrayfish.device.block.entity.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.mrcrayfish.device.block.PrinterBlock;
import com.mrcrayfish.device.block.RouterBlock;
import com.mrcrayfish.device.block.entity.RouterBlockEntity;
import com.mrcrayfish.device.core.network.NetworkDevice;
import com.mrcrayfish.device.core.network.Router;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.util.Collection;
import java.util.Objects;

/**
 * Author: MrCrayfish
 */
public record RouterRenderer(
        BlockEntityRendererProvider.Context context) implements BlockEntityRenderer<RouterBlockEntity> {

    @Override
    public void render(RouterBlockEntity blockEntity, float partialTick, @NotNull PoseStack pose, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = Objects.requireNonNull(blockEntity.getLevel()).getBlockState(blockEntity.getBlockPos());
        if (state.getBlock() != blockEntity.getBlock()) return;

        if (blockEntity.isDebug()) {
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(770, 771, 1, 0);
//            RenderSystem.disableLighting();
            RenderSystem.disableTexture();
//            RenderSystem.enableAlpha();
            pose.pushPose();
            {
                pose.translate(blockEntity.getBlockPos().getX(), blockEntity.getBlockPos().getY(), blockEntity.getBlockPos().getZ());
                Router router = blockEntity.getRouter();
                BlockPos routerPos = router.getPos();

                Vec3 linePositions = getLineStartPosition(state);
                final double startLineX = linePositions.x;
                final double startLineY = linePositions.y;
                final double startLineZ = linePositions.z;

                Tesselator tessellator = Tesselator.getInstance();
                BufferBuilder buffer = tessellator.getBuilder();

                final Collection<NetworkDevice> DEVICES = router.getConnectedDevices(Minecraft.getInstance().level);
                DEVICES.forEach(networkDevice -> {
                    BlockPos devicePos = networkDevice.getPos();

                    GL11.glLineWidth(14F);
                    buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
                    buffer.vertex(startLineX, startLineY, startLineZ).color(0.0F, 0.0F, 0.0F, 0.5F).endVertex();
                    buffer.vertex((devicePos.getX() - routerPos.getX()) + 0.5F, (devicePos.getY() - routerPos.getY()), (devicePos.getZ() - routerPos.getZ()) + 0.5F).color(1.0F, 1.0F, 1.0F, 0.35F).endVertex();
                    tessellator.end();

                    GL11.glLineWidth(4F);
                    buffer.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR);
                    buffer.vertex(startLineX, startLineY, startLineZ).color(0.0F, 0.0F, 0.0F, 0.5F).endVertex();
                    buffer.vertex((devicePos.getX() - routerPos.getX()) + 0.5F, (devicePos.getY() - routerPos.getY()), (devicePos.getZ() - routerPos.getZ()) + 0.5F).color(0.0F, 1.0F, 0.0F, 0.5F).endVertex();
                    tessellator.end();
                });
            }
            pose.popPose();
            RenderSystem.disableBlend();
//            RenderSystem.disableAlpha();
//            RenderSystem.enableLighting();
            RenderSystem.enableTexture();
        }
    }

    private Vec3 getLineStartPosition(BlockState state) {
        float lineX = 0.5F;
        float lineY = 0.1F;
        float lineZ = 0.5F;

        if (state.getValue(RouterBlock.VERTICAL)) {
            Quaternion rotation = state.getValue(PrinterBlock.FACING).getRotation();
            rotation.mul(new Quaternion((float) (14 * 0.0625), 0.5F, (float) (14 * 0.0625), 0.5F));
            Vector3f fixedPosition = rotation.toXYZ();
            lineX = fixedPosition.x();
            lineY = 0.35F;
            lineZ = fixedPosition.z();
        }

        return new Vec3(lineX, lineY, lineZ);
    }
}
