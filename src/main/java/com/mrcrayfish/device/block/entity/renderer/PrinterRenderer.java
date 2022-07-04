package com.mrcrayfish.device.block.entity.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.api.print.IPrint;
import com.mrcrayfish.device.api.print.PrintingManager;
import com.mrcrayfish.device.block.PrinterBlock;
import com.mrcrayfish.device.block.entity.PrinterBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Objects;

/**
 * Author: MrCrayfish
 */
public record PrinterRenderer(
        BlockEntityRendererProvider.Context context) implements BlockEntityRenderer<PrinterBlockEntity> {
    private static final PaperModel MODEL_PAPER = new PaperModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.BOOK));

    @Override
    public void render(PrinterBlockEntity blockEntity, float partialTick, @NotNull PoseStack pose, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = Objects.requireNonNull(blockEntity.getLevel()).getBlockState(blockEntity.getBlockPos());
        if (state.getBlock() != blockEntity.getBlock()) return;

        pose.pushPose();
        {
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            pose.translate(blockEntity.getBlockPos().getX(), blockEntity.getBlockPos().getY(), blockEntity.getBlockPos().getZ());

            if (blockEntity.hasPaper()) {
                pose.pushPose();
                {
                    pose.translate(0.5, 0.5, 0.5);
                    pose.mulPose(state.getValue(PrinterBlock.FACING).getRotation());
                    pose.mulPose(new Quaternion(1, 0, 0, 22.5f));
                    pose.translate(0, 0, 0.4);
                    pose.translate(-11 * 0.015625, -13 * 0.015625, -0.5 * 0.015625);

                    MultiBufferSource.BufferSource modelBufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
                    VertexConsumer vertexconsumer = modelBufferSource.getBuffer(MODEL_PAPER.renderType(PaperModel.TEXTURE));
                    MODEL_PAPER.renderToBuffer(pose, vertexconsumer, 15728880, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
                }
                pose.popPose();
            }

            pose.pushPose();
            {
                if (blockEntity.isLoading()) {
                    pose.translate(0.5, 0.5, 0.5);
                    pose.mulPose(state.getValue(PrinterBlock.FACING).getRotation());
                    pose.mulPose(new Quaternion(1, 0, 0, 22.5f));
                    double progress = Math.max(-0.4, -0.4 + (0.4 * ((double) (blockEntity.getRemainingPrintTime() - 10) / 20)));
                    pose.translate(0, progress, 0.36875);
                    pose.translate(-11 * 0.015625, -13 * 0.015625, -0.5 * 0.015625);
                    MultiBufferSource.BufferSource modelBufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
                    VertexConsumer vertexconsumer = modelBufferSource.getBuffer(MODEL_PAPER.renderType(PaperModel.TEXTURE));
                    MODEL_PAPER.renderToBuffer(pose, vertexconsumer, 15728880, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
                } else if (blockEntity.isPrinting()) {
                    pose.translate(0.5, 0.078125, 0.5);
                    pose.mulPose(state.getValue(PrinterBlock.FACING).getRotation());
                    pose.mulPose(new Quaternion(1, 0, 0, 90f));
                    double progress = -0.35 + (0.50 * ((double) (blockEntity.getRemainingPrintTime() - 20) / blockEntity.getTotalPrintTime()));
                    pose.translate(0, progress, 0);
                    pose.translate(-11 * 0.015625, -13 * 0.015625, -0.5 * 0.015625);
                    MultiBufferSource.BufferSource modelBufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
                    VertexConsumer vertexconsumer = modelBufferSource.getBuffer(MODEL_PAPER.renderType(PaperModel.TEXTURE));
                    MODEL_PAPER.renderToBuffer(pose, vertexconsumer, 15728880, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);

                    pose.translate(0.3225, 0.085, -0.001);
                    pose.mulPose(new Quaternion(0, 1, 0, 180f));
                    pose.scale(0.3f, 0.3f, 0.3f);

                    IPrint print = blockEntity.getPrint();
                    if (print != null) {
                        IPrint.Renderer renderer = PrintingManager.getRenderer(print);
                        renderer.render(print.toTag());
                    }
                }
            }
            pose.popPose();

            pose.pushPose();
            {
                RenderSystem.depthMask(false);
                pose.translate(0.5, 0.5, 0.5);
                pose.mulPose(state.getValue(PrinterBlock.FACING).getRotation());
                pose.mulPose(new Quaternion(0, 1, 0, 180f));
                pose.translate(0.0675, 0.005, -0.032);
                pose.translate(-6.5 * 0.0625, -3.5 * 0.0625, 3.01 * 0.0625);
                pose.scale(0.010416667f, -0.010416667f, 0.010416667f);
                GL11.glNormal3f(0f, 0f, -0.010416667f);
                pose.mulPose(new Quaternion(1, 0, 0, 22.5f));
                Minecraft.getInstance().font.draw(pose, Integer.toString(blockEntity.getPaperCount()), 0, 0, Color.WHITE.getRGB());
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                RenderSystem.depthMask(true);
            }
            pose.popPose();
        }
        pose.popPose();

        pose.pushPose();
        {
            pose.translate(0, -0.5, 0);

            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, pose, bufferSource, packedLight, packedOverlay, EmptyModelData.INSTANCE);
//                super.render(blockEntity, x, y, z, partialTicks, destroyStage, alpha);
        }
        pose.popPose();
    }

    public static class PaperModel extends Model {
        public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/model/paper.png");
        public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(MrCrayfishDeviceMod.res("paper_model"), "main");
        private final ModelPart root;
        private final ModelPart main;

//            private final ModelRenderer box = new ModelRenderer(this, 0, 0).addBox(0, 0, 0, 22, 30, 1);

        public PaperModel(ModelPart pRoot) {
            super(RenderType::entitySolid);
            this.root = pRoot;
            this.main = pRoot.getChild("main");
        }

        public static LayerDefinition createBodyLayer() {
            MeshDefinition meshdefinition = new MeshDefinition();
            PartDefinition partdefinition = meshdefinition.getRoot();
            partdefinition.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 0).addBox(0, 0, 0, 22, 30, 1), PartPose.offset(0f, 0f, 0f));
            return LayerDefinition.create(meshdefinition, 64, 32);
        }

        @Override
        public void renderToBuffer(@NotNull PoseStack pPoseStack, @NotNull VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
            this.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        }

        private void render(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
            RenderSystem.setShaderTexture(0, TEXTURE);
            this.root.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        }

        public ModelPart getMain() {
            return main;
        }
    }
}