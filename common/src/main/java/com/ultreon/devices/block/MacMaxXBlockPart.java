package com.ultreon.devices.block;

import com.ultreon.devices.block.entity.MacMaxXBlockEntity;
import dev.architectury.platform.Platform;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MacMaxXBlockPart extends ComputerBlock {
    private static EnumProperty<Part> PART = EnumProperty.create("part", Part.class);

    private static final VoxelShape BL_SHAPE_NORTH = Block.box(-16 + 16, 0, 7, 32 + 16, 35.25, 11);
    private static final VoxelShape BL_SHAPE_EAST = Block.box(-16 + 16, 0, 7, 32 + 16, 35.25, 11);
    private static final VoxelShape BL_SHAPE_SOUTH = Block.box(-16 + 16, 0, 7, 32 + 16, 35.25, 11);
    private static final VoxelShape BL_SHAPE_WEST = Block.box(-16 + 16, 0, 7, 32 + 16, 35.25, 11);

    private static final VoxelShape BR_SHAPE_NORTH = Block.box(-16 - 16, 0, 7, 32 - 16, 35.25, 11);
    private static final VoxelShape BR_SHAPE_EAST = Block.box(-16 - 16, 0, 7, 32 - 16, 35.25, 11);
    private static final VoxelShape BR_SHAPE_SOUTH = Block.box(-16 - 16, 0, 7, 32 - 16, 35.25, 11);
    private static final VoxelShape BR_SHAPE_WEST = Block.box(-16 - 16, 0, 7, 32 - 16, 35.25, 11);

    private static final VoxelShape TL_SHAPE_NORTH = Block.box(-16 + 16, -16, 7, 32 + 16, 19.25, 11);
    private static final VoxelShape TL_SHAPE_EAST = Block.box(-16 + 16, -16, 7, 32 + 16, 19.25, 11);
    private static final VoxelShape TL_SHAPE_SOUTH = Block.box(-16 + 16, -16, 7, 32 + 16, 19.25, 11);
    private static final VoxelShape TL_SHAPE_WEST = Block.box(-16 + 16, -16, 7, 32 + 16, 19.25, 11);

    private static final VoxelShape T_SHAPE_NORTH = Block.box(-16, -16, 7, 32, 19.25, 11);
    private static final VoxelShape T_SHAPE_EAST = Block.box(-16, -16, 7, 32, 19.25, 11);
    private static final VoxelShape T_SHAPE_SOUTH = Block.box(-16, -16, 7, 32, 19.25, 11);
    private static final VoxelShape T_SHAPE_WEST = Block.box(-16, -16, 7, 32, 19.25, 11);

    private static final VoxelShape TR_SHAPE_NORTH = Block.box(-16 - 16, -16, 7, 32 - 16, 19.25, 11);
    private static final VoxelShape TR_SHAPE_EAST = Block.box(-16 - 16, -16, 7, 32 - 16, 19.25, 11);
    private static final VoxelShape TR_SHAPE_SOUTH = Block.box(-16 - 16, -16, 7, 32 - 16, 19.25, 11);
    private static final VoxelShape TR_SHAPE_WEST = Block.box(-16 - 16, -16, 7, 32 - 16, 19.25, 11);

    public MacMaxXBlockPart() {
        super(Properties.of(Material.HEAVY_METAL, DyeColor.WHITE).strength(6f).sound(SoundType.METAL));
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        switch (pState.getValue(PART)) {
            case BL -> {
                return switch (pState.getValue(FACING)) {
                    case NORTH -> BL_SHAPE_NORTH;
                    case EAST -> BL_SHAPE_EAST;
                    case SOUTH -> BL_SHAPE_SOUTH;
                    case WEST -> BL_SHAPE_WEST;
                    default -> throw new IllegalStateException("Unexpected value: " + pState.getValue(FACING));
                };
            }
            case BR -> {
                return switch (pState.getValue(FACING)) {
                    case NORTH -> BR_SHAPE_NORTH;
                    case EAST -> BR_SHAPE_EAST;
                    case SOUTH -> BR_SHAPE_SOUTH;
                    case WEST -> BR_SHAPE_WEST;
                    default -> throw new IllegalStateException("Unexpected value: " + pState.getValue(FACING));
                };
            }
            case TL -> {
                return switch (pState.getValue(FACING)) {
                    case NORTH -> TL_SHAPE_NORTH;
                    case EAST -> TL_SHAPE_EAST;
                    case SOUTH -> TL_SHAPE_SOUTH;
                    case WEST -> TL_SHAPE_WEST;
                    default -> throw new IllegalStateException("Unexpected value: " + pState.getValue(FACING));
                };
            }
            case T -> {
                return switch (pState.getValue(FACING)) {
                    case NORTH -> T_SHAPE_NORTH;
                    case EAST -> T_SHAPE_EAST;
                    case SOUTH -> T_SHAPE_SOUTH;
                    case WEST -> T_SHAPE_WEST;
                    default -> throw new IllegalStateException("Unexpected value: " + pState.getValue(FACING));
                };
            }
            case TR -> {
                return switch (pState.getValue(FACING)) {
                    case NORTH -> TR_SHAPE_NORTH;
                    case EAST -> TR_SHAPE_EAST;
                    case SOUTH -> TR_SHAPE_SOUTH;
                    case WEST -> TR_SHAPE_WEST;
                    default -> throw new IllegalStateException("Unexpected value: " + pState.getValue(FACING));
                };
            }
        };
        throw new IllegalStateException("Unexpected value: " + pState.getValue(PART));
    }

    @Override
    public boolean isDesktopPC() {
        return true;
    }

    @Override
    public MutableComponent getName() {
        MutableComponent normalName = Component.translatable("block.devices.mac_max_x");
        if (Platform.isModLoaded("emojiful")) {
            return Component.translatable("block.devices.mac_max_x_emoji");
        }
        return normalName;
    }

    @Override
    public RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new MacMaxXBlockEntity(pos, state);
    }

    public enum Part implements StringRepresentable {
        BL, BR, TL, T, TR;

        @Override
        public String getSerializedName() {
            return switch (this) {
                case BL -> "bottom_left";
                case BR -> "bottom_right";
                case TL -> "top_left";
                case T -> "top";
                case TR -> "top_right";
            };
        }
    }
}
