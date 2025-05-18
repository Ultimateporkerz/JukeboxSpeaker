package net.ultimporks.betterdiscs.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CeilingSpeakerBlock extends SpeakerBlock {
    public static final VoxelShape SHAPE = Block.box(4, 14, 4, 12, 16, 12);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public CeilingSpeakerBlock(Properties pProperties) {
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
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

}
