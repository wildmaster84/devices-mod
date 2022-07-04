package com.mrcrayfish.device.block;

import com.mrcrayfish.device.block.entity.LaptopBlockEntity;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.item.FlashDriveItem;
import com.mrcrayfish.device.util.BlockEntityUtil;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class LaptopBlock extends DeviceBlock.Colored {
    public static final EnumProperty<Type> TYPE = EnumProperty.create("type", Type.class);

    private static final VoxelShape[] SCREEN_BOXES = new VoxelShape[]{
            box(13, 1, 1, 15, 12, 15),
            box(1, 1, 13, 15, 12, 15),
            box(1, 1, 1, 3, 12, 15),
            box(1, 1, 1, 15, 12, 3)
    };
    private static final VoxelShape BODY_OPEN_BOX = Block.box(1, 0, 1, 13, 1, 15);
    private static final VoxelShape BODY_CLOSED_BOX = Block.box(1, 0, 1, 13, 2, 15);
    private static final VoxelShape SELECTION_BOX_OPEN = Block.box(0, 0, 0, 16, 12, 16);
    private static final VoxelShape SELECTION_BOX_CLOSED = Block.box(0, 0, 0, 16, 3, 16);

    public LaptopBlock(DyeColor color) {
        super(BlockBehaviour.Properties.of(Material.HEAVY_METAL, color), color);
        registerDefaultState(this.getStateDefinition().any().setValue(TYPE, Type.BASE));
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (blockEntity instanceof LaptopBlockEntity laptop) {
            if (laptop.isOpen()) return BODY_OPEN_BOX;
            else return BODY_CLOSED_BOX;
        } else {
            return SELECTION_BOX_OPEN;
        }
    }

    @Override
    public InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity instanceof LaptopBlockEntity laptop) {
            if (player.isCrouching()) {
                if (!level.isClientSide) {
                    laptop.openClose();
                }
            } else {
                if (hit.getDirection() == state.getValue(FACING).getCounterClockWise(Direction.Axis.Y)) {
                    ItemStack heldItem = player.getItemInHand(hand);
                    if (!heldItem.isEmpty() && heldItem.getItem() instanceof FlashDriveItem) {
                        if (!level.isClientSide) {
                            if (laptop.getFileSystem().setAttachedDrive(heldItem.copy())) {
                                heldItem.shrink(1);
                            } else {
                                player.sendMessage(new TextComponent("No more available USB slots!"), Util.NIL_UUID);
                            }
                        }
                        return InteractionResult.CONSUME;
                    }

                    if (!level.isClientSide) {
                        ItemStack stack = laptop.getFileSystem().removeAttachedDrive();
                        if (stack != null) {
                            BlockPos summonPos = pos.relative(state.getValue(FACING).getCounterClockWise(Direction.Axis.Y));
                            level.addFreshEntity(new ItemEntity(level, summonPos.getX() + 0.5, summonPos.getY(), summonPos.getZ() + 0.5, stack));
                            BlockEntityUtil.markBlockForUpdate(level, pos);
                        }
                    }
                    return InteractionResult.SUCCESS;
                }

                if (laptop.isOpen() && level.isClientSide) {
                    Minecraft.getInstance().setScreen(new Laptop(laptop));
//                    playerIn.openMenu(new SimpleMenuProvider(), MrCrayfishDeviceMod.instance, Laptop.ID, worldIn, pos.getX(), pos.getY(), pos.getZ());
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void removeTagsForDrop(CompoundTag tileEntityTag) {
        tileEntityTag.remove("open");
    }

//    @Override
//    public BlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
//        return super.getActualState(state, worldIn, pos).withProperty(TYPE, Type.BASE);
//    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new LaptopBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(TYPE);
    }

    public enum Type implements StringRepresentable {
        BASE, SCREEN;

        @NotNull
        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
