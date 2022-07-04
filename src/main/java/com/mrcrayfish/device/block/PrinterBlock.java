package com.mrcrayfish.device.block;

import com.mrcrayfish.device.block.entity.PrinterBlockEntity;
import com.mrcrayfish.device.util.IHasColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Author: MrCrayfish
 */
public class PrinterBlock extends DeviceBlock.Colored implements IHasColor {
    // Box(...) functions have (x1, y1, z1, x2, y2, z2) as parameters
    // Every voxel shape is rotated 90 degrees around the Y axis to match the block's orientation
    private static final VoxelShape[] BODY_BOUNDING_BOX = {
            box(5, 0d, 1, 14, 5, 15), // North
            box(1, 0d, 5, 15, 5, 14), // East
            box(2, 0d, 1, 11, 5, 15), // South
            box(1, 0d, 2, 15, 5, 11), // West
    };

    private static final VoxelShape[] TRAY_BOUNDING_BOX = {
            box(0.5, 0, 3.5, 5, 1, 12.5), // North
            box(3.5, 0, 0.5, 12.5, 1, 5), // East
            box(11.5, 0, 3.5, 15.5, 0, 12.5), // South
            box(3.5, 0, 11.5, 12.5, 1, 15.5) // West
    };

    private static final VoxelShape[] PAPER_BOUNDING_BOX = {
            box(1, 0d, 4, 13d, 9, 12), // North
            box(4, 0d, 1, 12, 9, 13d), // East
            box(2, 0d, 4, 15d, 9, 12), // South
            box(4, 0d, 2, 12, 9, 15d) // West
    };

//    private static final AxisAlignedBB[] TRAY_BOUNDING_BOX = new Bounds(0.5, 0, 3.5, 5, 1, 12.5).getRotatedBounds();
//    private static final AxisAlignedBB[] PAPER_BOUNDING_BOX = new Bounds(13, 0.0, 4, 1.0, 9, 12).getRotatedBounds();
//
//    private static final AxisAlignedBB SELECTION_BOUNDING_BOX = new AxisAlignedBB(0, 0, 0, 16, 8, 16);

    private static final VoxelShape SHAPE_NORTH = Shapes.or(
            Block.box(2, 0, 7, 14, 5, 12),
            Block.box(3.5, 0.1, 1, 12.5, 1.1, 7),
            Block.box(12, 0, 12, 15, 5, 14),
            Block.box(12, 0, 5, 15, 3, 7),
            Block.box(1, 0, 5, 4, 3, 7),
            Block.box(1, 0, 12, 4, 5, 14),
            Block.box(1.1, 0, 7, 14.9, 5, 12),
            Block.box(4, 0, 12, 12, 3, 14),
            Block.box(3.5, 0.1, 1, 12.5, 1.1, 7.5),
            Block.box(1, 3, 5, 15, 5, 7),
            Block.box(4, 3, 12, 12, 9.3, 16));
    private static final VoxelShape SHAPE_EAST = Shapes.or(
            Block.box(4, 0, 2, 9, 5, 14),
            Block.box(9, 0.1, 3.5, 15, 1.1, 12.5),
            Block.box(2, 0, 12, 4, 5, 15),
            Block.box(9, 0, 12, 11, 3, 15),
            Block.box(9, 0, 1, 11, 3, 4),
            Block.box(2, 0, 1, 4, 5, 4),
            Block.box(4, 0, 1.1, 9, 5, 14.9),
            Block.box(2, 0, 4, 4, 3, 12),
            Block.box(8.5, 0.1, 3.5, 15, 1.1, 12.5),
            Block.box(9, 3, 1, 11, 5, 15),
            Block.box(0, 3, 4, 4, 9.3, 12));
    private static final VoxelShape SHAPE_SOUTH = Shapes.or(
            Block.box(2, 0, 4, 14, 5, 9),
            Block.box(3.5, 0.1, 9, 12.5, 1.1, 15),
            Block.box(1, 0, 2, 4, 5, 4),
            Block.box(1, 0, 9, 4, 3, 11),
            Block.box(12, 0, 9, 15, 3, 11),
            Block.box(12, 0, 2, 15, 5, 4),
            Block.box(1.1, 0, 4, 14.9, 5, 9),
            Block.box(4, 0, 2, 12, 3, 4),
            Block.box(3.5, 0.1, 8.5, 12.5, 1.1, 15),
            Block.box(1, 3, 9, 15, 5, 11),
            Block.box(4, 3, 0, 12, 9.3, 3.4));
    private static final VoxelShape SHAPE_WEST = Shapes.or(
            Block.box(7, 0, 2, 12, 5, 14),
            Block.box(1, 0.1, 3.5, 7, 1.1, 12.5),
            Block.box(12, 0, 1, 14, 5, 4),
            Block.box(5, 0, 1, 7, 3, 4),
            Block.box(5, 0, 12, 7, 3, 15),
            Block.box(12, 0, 12, 14, 5, 15),
            Block.box(7, 0, 1.1, 12, 5, 14.9),
            Block.box(12, 0, 4, 14, 3, 12),
            Block.box(1, 0.1, 3.5, 7.5, 1.1, 12.5),
            Block.box(5, 3, 1, 7, 5, 15),
            Block.box(12, 3, 4, 16, 9.3, 12));

    public PrinterBlock(DyeColor color) {
        super(Properties.of(Material.HEAVY_METAL, color).strength(6f).sound(SoundType.METAL), color);
        this.registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }

    @NotNull
    @Override
    public VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return switch (pState.getValue(FACING)) {
            case NORTH -> SHAPE_NORTH;
            case EAST -> SHAPE_EAST;
            case SOUTH -> SHAPE_SOUTH;
            case WEST -> SHAPE_WEST;
            default -> throw new IllegalStateException("Unexpected value: " + pState.getValue(FACING));
        };
    }

//    @Override
//    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
//    {
//        return SELECTION_BOUNDING_BOX;
//    }
//
//    @Override
//    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean p_185477_7_)
//    {
//        EnumFacing facing = state.getValue(FACING);
//        Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, BODY_BOUNDING_BOX[facing.getHorizontalIndex()]);
//        Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, TRAY_BOUNDING_BOX[facing.getHorizontalIndex()]);
//        Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, PAPER_BOUNDING_BOX[facing.getHorizontalIndex()]);
//    }


    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);
        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity instanceof PrinterBlockEntity) {
            return ((PrinterBlockEntity) tileEntity).addPaper(heldItem, player.isCrouching()) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
        }
        return InteractionResult.PASS;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new PrinterBlockEntity(pos, state);
    }
}
