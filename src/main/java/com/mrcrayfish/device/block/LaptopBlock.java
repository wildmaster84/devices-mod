package com.mrcrayfish.device.block;

import com.mrcrayfish.device.DeviceType;
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
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class LaptopBlock extends DeviceBlock.Colored {
    public static final EnumProperty<Type> TYPE = EnumProperty.create("type", Type.class);

    private static final VoxelShape SHAPE_OPEN_NORTH = Shapes.or(Block.box(1, 0, 12.5, 15, 11.4, 17), Block.box(1, 0, 1, 15, 1.3, 12.5));
    private static final VoxelShape SHAPE_OPEN_EAST = Shapes.or(Block.box(-1, 0, 1, 3.5, 11.4, 15), Block.box(3.5, 0, 1, 15, 1.3, 15));
    private static final VoxelShape SHAPE_OPEN_SOUTH = Shapes.or(Block.box(1, 0, -1, 15, 11.4, 3.5), Block.box(1, 0, 3.5, 15, 1.3, 15));
    private static final VoxelShape SHAPE_OPEN_WEST = Shapes.or(Block.box(12.5, 0, 1, 17, 11.4, 15), Block.box(1, 0, 1, 12.5, 1.3, 15));
    private static final VoxelShape SHAPE_CLOSED_NORTH = Shapes.or(Block.box(1, 0, -1, 15, 11.4, 3.5), Block.box(1, 0, 3.5, 15, 1.3, 15));
    private static final VoxelShape SHAPE_CLOSED_EAST = Shapes.or(Block.box(12.5, 0, 1, 17, 11.4, 15), Block.box(1, 0, 1, 12.5, 1.3, 15));
    private static final VoxelShape SHAPE_CLOSED_SOUTH = Shapes.or(Block.box(1, 0, 12.5, 15, 11.4, 17), Block.box(1, 0, 1, 15, 1.3, 12.5));
    private static final VoxelShape SHAPE_CLOSED_WEST = Shapes.or(Block.box(-1, 0, 1, 3.5, 11.4, 15), Block.box(3.5, 0, 1, 15, 1.3, 15));

    public LaptopBlock(DyeColor color) {
        super(BlockBehaviour.Properties.of(Material.HEAVY_METAL, color).strength(6.0f).sound(SoundType.METAL), color, DeviceType.LAPTOP);
        registerDefaultState(this.getStateDefinition().any().setValue(TYPE, Type.BASE));
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return switch (pState.getValue(FACING)) {
            case NORTH -> SHAPE_OPEN_NORTH;
            case EAST -> SHAPE_OPEN_EAST;
            case SOUTH -> SHAPE_OPEN_SOUTH;
            case WEST -> SHAPE_OPEN_WEST;
            default -> throw new IllegalStateException("Unexpected value: " + pState.getValue(FACING));
        };
    }

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        System.out.println("Using a laptop, block entity is " + blockEntity);
        if (blockEntity instanceof LaptopBlockEntity laptop) {
            if (player.isCrouching()) {
                System.out.println("Using a laptop, player is crouching.");
                if (!level.isClientSide) {
                    System.out.println("Using a laptop, player is crouching, not client side.");
                    laptop.openClose();
                    System.out.println("Using a laptop, player is crouching, not client side, laptop is open: " + laptop.isOpen());
                }
                return InteractionResult.SUCCESS;
            } else {
                System.out.println("Using a laptop, player is not crouching.");
                if (hit.getDirection() == state.getValue(FACING).getCounterClockWise(Direction.Axis.Y)) {
                    System.out.println("Using a laptop, player is not crouching, hit direction is valid.");
                    ItemStack heldItem = player.getItemInHand(hand);
                    if (!heldItem.isEmpty() && heldItem.getItem() instanceof FlashDriveItem) {
                        System.out.println("Using a laptop, player is not crouching, hit direction is valid, held item is flash drive.");
                        if (!level.isClientSide) {
                            System.out.println("Using a laptop, player is not crouching, hit direction is valid, held item is flash drive, not client side.");
                            if (laptop.getFileSystem().setAttachedDrive(heldItem.copy())) {
                                System.out.println("Using a laptop, player is not crouching, hit direction is valid, held item is flash drive, not client side, drive set.");
                                heldItem.shrink(1);
                                return InteractionResult.CONSUME;
                            } else {
                                player.sendMessage(new TextComponent("No more available USB slots!"), Util.NIL_UUID);
                                return InteractionResult.FAIL;
                            }
                        }
                        return InteractionResult.PASS;
                    }

                    if (!level.isClientSide) {
                        System.out.println("Using a laptop, player is not crouching, hit direction is valid, held item is not flash drive, not client side.");
                        ItemStack stack = laptop.getFileSystem().removeAttachedDrive();
                        if (stack != null) {
                            System.out.println("Using a laptop, player is not crouching, hit direction is valid, held item is not flash drive, not client side, stack is not null.");
                            BlockPos summonPos = pos.relative(state.getValue(FACING).getCounterClockWise(Direction.Axis.Y));
                            System.out.println("Using a laptop, player is not crouching, hit direction is valid, held item is not flash drive, not client side, stack is not null, summon pos: " + summonPos);
                            level.addFreshEntity(new ItemEntity(level, summonPos.getX() + 0.5, summonPos.getY(), summonPos.getZ() + 0.5, stack));
                            System.out.println("Using a laptop, player is not crouching, hit direction is valid, held item is not flash drive, not client side, stack is not null, item entity added.");
                            BlockEntityUtil.markBlockForUpdate(level, pos);
                            System.out.println("Using a laptop, player is not crouching, hit direction is valid, held item is not flash drive, not client side, stack is not null, marked for update.");
                        }
                    }
                    return InteractionResult.SUCCESS;
                }

                if (laptop.isOpen() && level.isClientSide) {
                    System.out.println("Using a laptop, player is not crouching, hit direction is not valid, laptop is open, client side.");
                    Minecraft.getInstance().setScreen(new Laptop(laptop));
                    System.out.println("Using a laptop, player is not crouching, hit direction is not valid, laptop is open, client side, pushed gui layer.");
                    return InteractionResult.SUCCESS;
//                    player.openMenu(new SimpleMenuProvider(), MrCrayfishDeviceMod.instance, Laptop.ID, worldIn, pos.getX(), pos.getY(), pos.getZ());
                }
                System.out.println("Using a laptop, player is not crouching, hit direction is not valid, laptop is not open.");
            }
        }

        System.out.println("Using a laptop, block entity is not laptop.");

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
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> pBuilder) {
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
