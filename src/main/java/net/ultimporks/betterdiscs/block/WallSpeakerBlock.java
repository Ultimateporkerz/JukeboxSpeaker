package net.ultimporks.betterdiscs.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumMap;

public class WallSpeakerBlock extends SpeakerBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final EnumMap<Direction, VoxelShape> SHAPES = new EnumMap<>(Direction.class);

    static {
        // Create shapes for each direction
        SHAPES.put(Direction.SOUTH, Block.box(4, 4, 15, 12, 12, 16)); // Original shape (NORTH)
        SHAPES.put(Direction.NORTH, Block.box(4, 4, 0, 12, 12, 1));   // Rotated for SOUTH
        SHAPES.put(Direction.EAST, Block.box(15, 4, 4, 16, 12, 12));  // Rotated for WEST
        SHAPES.put(Direction.WEST, Block.box(0, 4, 4, 1, 12, 12));    // Rotated for EAST
    }

    public WallSpeakerBlock(Properties pProperties) {
        super(pProperties);
    }


    @Override
    public boolean isSignalSource(BlockState pState) {
        return false;
    }
    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Direction direction = pContext.getHorizontalDirection();
        return this.defaultBlockState().setValue(FACING, direction);
    }
    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPES.getOrDefault(pState.getValue(FACING), SHAPES.get(Direction.NORTH));
    }

}
