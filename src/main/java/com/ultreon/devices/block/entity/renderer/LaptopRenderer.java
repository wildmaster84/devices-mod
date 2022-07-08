package com.ultreon.devices.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Quaternion;
import com.ultreon.devices.block.LaptopBlock;
import com.ultreon.devices.block.entity.LaptopBlockEntity;
import com.ultreon.devices.init.DeviceItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Random;

public class LaptopRenderer implements BlockEntityRenderer<LaptopBlockEntity> {
    private final Minecraft mc = Minecraft.getInstance();

    private final BlockEntityRendererProvider.Context context;

    static {
        assert Minecraft.getInstance().level != null;
    }

    public LaptopRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(LaptopBlockEntity blockEntity, float partialTick, PoseStack pose, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = blockEntity.getBlock().defaultBlockState().setValue(LaptopBlock.TYPE, LaptopBlock.Type.SCREEN);
        BlockPos pos = blockEntity.getBlockPos();
        ItemEntity itemEntity = new ItemEntity(Objects.requireNonNull(blockEntity.getLevel()), 0D, 0D, 0D, ItemStack.EMPTY);

        Tesselator tesselator = Tesselator.getInstance();
//		RenderSystem.setShaderTexture(0, TextureMapping.LOCATION_BLOCKS_TEXTURE);
        pose.pushPose();
        {
            pose.translate(pos.getX(), pos.getY(), pos.getZ());

            if (blockEntity.isExternalDriveAttached()) {
                pose.pushPose();
                {
                    pose.translate(0.5, 0, 0.5);
                    pose.mulPose(blockEntity.getBlockState().getValue(LaptopBlock.FACING).getRotation());
                    pose.translate(-0.5, 0, -0.5);
                    pose.translate(0.595, -0.2075, -0.005);
                    pose.translate(0, -2, 0);
//                    itemEntity.bobOffs = 0f;
//                    itemEntity.setItem(new ItemStack(DeviceItems.getFlashDriveByColor(blockEntity.getExternalDriveColor())));
//                    Minecraft.getInstance().getEntityRenderDispatcher().render(itemEntity, 0.0D, 0.0D, 0.0D, 0f, 0f, pose, bufferSource, packedLight);
                    ItemStack itemStack = new ItemStack(DeviceItems.getFlashDriveByColor(blockEntity.getExternalDriveColor()));
                    Minecraft.getInstance().getItemRenderer().renderStatic(itemStack, ItemTransforms.TransformType.GROUND, packedLight, packedOverlay, pose, MultiBufferSource.immediate(Tesselator.getInstance().getBuilder()), new Random().nextInt());
                    tesselator.end();
                    pose.translate(0.1, 0, 0);
                }
                pose.popPose();
            }

            pose.pushPose();
            {
                pose.translate(0.5, 0, 0.5);
                pose.mulPose(blockEntity.getBlockState().getValue(LaptopBlock.FACING).getRotation());
                pose.translate(-0.5, 0, -0.5);
                pose.translate(0, 0.0625, 0.25);
                pose.mulPose(new Quaternion(1, 0, 0, blockEntity.getScreenAngle(partialTick)));

//				GlStateManager.disableLighting();
//                BufferBuilder buffer = tesselator.getBuilder();
//                buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
//                buffer.setQuadSortOrigin(-pos.getX(), -pos.getY(), -pos.getZ());
////                BakedModel blockModel = mc.getBlockRenderer().getBlockModel(state);
//                buffer.setQuadSortOrigin(0f, 0f, 0f);
//                buffer.end();

                // Render the block itself
//                BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
//                BufferBuilder builder = tesselator.getBuilder();
//                blockRenderer.renderSingleBlock(state, pose, MultiBufferSource.immediate(builder), packedLight, packedOverlay, EmptyModelData.INSTANCE);
//                tesselator.end();

//				GlStateManager.enableLighting();
            }
            pose.popPose();
        }
        pose.popPose();
    }

    public BlockEntityRendererProvider.Context context() {
        return context;
    }
}
