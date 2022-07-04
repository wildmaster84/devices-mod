package com.mrcrayfish.device.block.entity.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Quaternion;
import com.mrcrayfish.device.DeviceConfig;
import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.api.print.IPrint;
import com.mrcrayfish.device.api.print.PrintingManager;
import com.mrcrayfish.device.block.PaperBlock;
import com.mrcrayfish.device.block.entity.PaperBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Author: MrCrayfish
 */
public record PaperRenderer(
        BlockEntityRendererProvider.Context context) implements BlockEntityRenderer<PaperBlockEntity> {

    @SuppressWarnings("SameParameterValue")
    private static void drawCuboid(double x, double y, double z, double width, double height, double depth) {
        x /= 16;
        y /= 16;
        z /= 16;
        width /= 16;
        height /= 16;
        depth /= 16;
//        RenderSystem.disableLighting();
//        GlStateManager.enableRescaleNormal();
//        pose.glNormal3f(0f, 1f, 0f);
        double v = x + width + 1 - (width + width);
        drawQuad(x + (1 - width), y, z, x + width + (1 - width), y + height, z, Direction.NORTH);
        drawQuad(x + 1, y, z, x + 1, y + height, z + depth, Direction.EAST);
        drawQuad(v, y, z + depth, v, y + height, z, Direction.WEST);
        drawQuad(x + (1 - width), y, z + depth, x + width + (1 - width), y, z, Direction.DOWN);
        drawQuad(x + (1 - width), y + height, z, x + width + (1 - width), y, z + depth, Direction.UP);
//        GlStateManager.disableRescaleNormal();
//        GlStateManager.enableLighting();
    }

    private static void drawQuad(double xFrom, double yFrom, double zFrom, double xTo, double yTo, double zTo, Direction direction) {
        double textureWidth = Math.abs(xTo - xFrom);
        double textureHeight = Math.abs(yTo - yFrom);
        double textureDepth = Math.abs(zTo - zFrom);

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        switch (direction.getAxis()) {
            case X -> {
                buffer.vertex(xFrom, yFrom, zFrom).uv((float) (1 - xFrom + textureDepth), (float) (1 - yFrom + textureHeight)).endVertex();
                buffer.vertex(xFrom, yTo, zFrom).uv((float) (1 - xFrom + textureDepth), (float) (1 - yFrom)).endVertex();
                buffer.vertex(xTo, yTo, zTo).uv((float) (1 - xFrom), (float) (1 - yFrom)).endVertex();
                buffer.vertex(xTo, yFrom, zTo).uv((float) (1 - xFrom), (float) (1 - yFrom + textureHeight)).endVertex();
            }
            case Y -> {
                buffer.vertex(xFrom, yFrom, zFrom).uv((float) (1 - xFrom + textureWidth), (float) (1 - yFrom + textureDepth)).endVertex();
                buffer.vertex(xFrom, yFrom, zTo).uv((float) (1 - xFrom + textureWidth), (float) (1 - yFrom)).endVertex();
                buffer.vertex(xTo, yFrom, zTo).uv((float) (1 - xFrom), (float) (1 - yFrom)).endVertex();
                buffer.vertex(xTo, yFrom, zFrom).uv((float) (1 - xFrom), (float) (1 - yFrom + textureDepth)).endVertex();
            }
            case Z -> {
                buffer.vertex(xFrom, yFrom, zFrom).uv((float) (1 - xFrom + textureWidth), (float) (1 - yFrom + textureHeight)).endVertex();
                buffer.vertex(xFrom, yTo, zFrom).uv((float) (1 - xFrom + textureWidth), (float) (1 - yFrom)).endVertex();
                buffer.vertex(xTo, yTo, zTo).uv((float) (1 - xFrom), (float) (1 - yFrom)).endVertex();
                buffer.vertex(xTo, yFrom, zTo).uv((float) (1 - xFrom), (float) (1 - yFrom + textureHeight)).endVertex();
            }
        }
        tessellator.end();
    }

    private static void drawPixels(int[] pixels, int resolution, boolean cut) {
        double scale = 16 / (double) resolution;
        for (int i = 0; i < resolution; i++) {
            for (int j = 0; j < resolution; j++) {
                float a = (float) Math.floor((pixels[j + i * resolution] >> 24 & 255) / 255f);
                if (a < 1f) {
                    if (cut) continue;
                    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                } else {
                    float r = (float) (pixels[j + i * resolution] >> 16 & 255) / 255f;
                    float g = (float) (pixels[j + i * resolution] >> 8 & 255) / 255f;
                    float b = (float) (pixels[j + i * resolution] & 255) / 255f;
                    RenderSystem.setShaderColor(r, g, b, a);
                }
                drawCuboid(j * scale - (resolution - 1) * scale, -i * scale + (resolution - 1) * scale, -1, scale, scale, 1);
            }
        }
    }

    @Override
    public void render(PaperBlockEntity blockEntity, float partialTick, @NotNull PoseStack pose, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = Objects.requireNonNull(blockEntity.getLevel()).getBlockState(blockEntity.getBlockPos());
        if (blockEntity.getBlockState().getBlock() != state.getBlock()) {
            MrCrayfishDeviceMod.LOGGER.error("Paper block mismatch: {} != {}", blockEntity.getBlockState().getBlock(), state.getBlock());
            return;
        }

        pose.pushPose();
        {
            pose.translate(blockEntity.getBlockPos().getX(), blockEntity.getBlockPos().getY(), blockEntity.getBlockPos().getZ());
            pose.translate(0.5, 0.5, 0.5);
            pose.mulPose(state.getValue(PaperBlock.FACING).getRotation());
            pose.mulPose(new Quaternion(0, 0, 1, -blockEntity.getRotation()));
            pose.translate(-0.5, -0.5, -0.5);

            IPrint print = blockEntity.getPrint();
            if (print != null) {
                CompoundTag data = print.toTag();
                if (data.contains("pixels", Tag.TAG_INT_ARRAY) && data.contains("resolution", Tag.TAG_INT)) {
                    RenderSystem.setShaderTexture(0, PrinterRenderer.PaperModel.TEXTURE);
                    if (DeviceConfig.RENDER_PRINTED_3D.get() && !data.getBoolean("cut")) {
                        drawCuboid(0, 0, 0, 16, 16, 1);
                    }

                    pose.translate(0, 0, DeviceConfig.RENDER_PRINTED_3D.get() ? 0.0625 : 0.001);

                    pose.pushPose();
                    {
                        IPrint.Renderer renderer = PrintingManager.getRenderer(print);
                        renderer.render(data);
                    }
                    pose.popPose();

                    pose.pushPose();
                    {
                        if (DeviceConfig.RENDER_PRINTED_3D.get() && data.getBoolean("cut")) {
                            CompoundTag tag = print.toTag();
                            drawPixels(tag.getIntArray("pixels"), tag.getInt("resolution"), tag.getBoolean("cut"));
                        }
                    }
                    pose.popPose();
                }
            }
        }
        pose.popPose();
    }
}
