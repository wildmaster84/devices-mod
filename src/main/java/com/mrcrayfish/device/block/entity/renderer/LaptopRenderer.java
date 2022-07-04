package com.mrcrayfish.device.block.entity.renderer;

import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Quaternion;
import com.mrcrayfish.device.block.LaptopBlock;
import com.mrcrayfish.device.block.entity.LaptopBlockEntity;
import com.mrcrayfish.device.init.DeviceItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;
import org.jetbrains.annotations.NotNull;

public class LaptopRenderer implements BlockEntityRenderer<LaptopBlockEntity> {
    private final Minecraft mc = Minecraft.getInstance();

    private final ItemEntity itemEntity;
    private final BlockEntityRendererProvider.Context context;

    {
        assert Minecraft.getInstance().level != null;
        itemEntity = new ItemEntity(Minecraft.getInstance().level, 0D, 0D, 0D, ItemStack.EMPTY);
    }

    public LaptopRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(LaptopBlockEntity blockEntity, float partialTick, PoseStack pose, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = blockEntity.getBlock().defaultBlockState().setValue(LaptopBlock.TYPE, LaptopBlock.Type.SCREEN);
        BlockPos pos = blockEntity.getBlockPos();

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
                    itemEntity.bobOffs = 0f;
                    itemEntity.setItem(new ItemStack(DeviceItems.getFlashDriveByColor(blockEntity.getExternalDriveColor())));
                    Minecraft.getInstance().getEntityRenderDispatcher().render(itemEntity, 0.0D, 0.0D, 0.0D, 0f, 0f, pose, bufferSource, packedLight);
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
                Tesselator tessellator = Tesselator.getInstance();
                BufferBuilder buffer = tessellator.getBuilder();
                buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
                buffer.setQuadSortOrigin(-pos.getX(), -pos.getY(), -pos.getZ());

                BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
                BakedModel blockModel = mc.getBlockRenderer().getBlockModel(state);
                blockRenderer.renderSingleBlock(state, pose, MultiBufferSource.immediate(buffer), packedLight, packedOverlay, EmptyModelData.INSTANCE);

                buffer.setQuadSortOrigin(0f, 0f, 0f);
                tessellator.end();
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
