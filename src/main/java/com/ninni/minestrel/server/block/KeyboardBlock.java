package com.ninni.minestrel.server.block;

import com.ninni.minestrel.server.block.entity.KeyboardBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class KeyboardBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);

    public KeyboardBlock() {
        super(Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false).setValue(FACING, Direction.NORTH));
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof KeyboardBlockEntity keyboardBlockEntity) {
                player.openMenu(keyboardBlockEntity);
                //player.awardStat(Stats.INTERACT_WITH_FURNACE);
            }
            return InteractionResult.CONSUME;
        }
    }

    public void setPlacedBy(Level p_52676_, BlockPos p_52677_, BlockState p_52678_, LivingEntity p_52679_, ItemStack p_52680_) {
        if (p_52680_.hasCustomHoverName()) {
            BlockEntity blockentity = p_52676_.getBlockEntity(p_52677_);
            if (blockentity instanceof KeyboardBlockEntity keyboardBlockEntity) {
                keyboardBlockEntity.setCustomName(p_52680_.getHoverName());
            }
        }
    }

    public void onRemove(BlockState blockState, Level level, BlockPos pos, BlockState state, boolean b) {
        if (!blockState.is(state.getBlock())) {
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof KeyboardBlockEntity keyboardBlockEntity) {
                Containers.dropContents(level, pos, keyboardBlockEntity);
                level.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(blockState, level, pos, state, b);
        }
    }

    public FluidState getFluidState(BlockState p_56397_) {
        return p_56397_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_56397_);
    }

    public BlockState updateShape(BlockState p_56381_, Direction p_56382_, BlockState p_56383_, LevelAccessor p_56384_, BlockPos p_56385_, BlockPos p_56386_) {
        if (p_56381_.getValue(WATERLOGGED)) {
            p_56384_.scheduleTick(p_56385_, Fluids.WATER, Fluids.WATER.getTickDelay(p_56384_));
        }
        return super.updateShape(p_56381_, p_56382_, p_56383_, p_56384_, p_56385_, p_56386_);
    }

    public VoxelShape getShape(BlockState p_56390_, BlockGetter p_56391_, BlockPos p_56392_, CollisionContext p_56393_) {
        return SHAPE;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_56388_) {
        p_56388_.add(WATERLOGGED, FACING);
    }

    public BlockState getStateForPlacement(BlockPlaceContext p_54481_) {
        return this.defaultBlockState().setValue(FACING, p_54481_.getHorizontalDirection().getOpposite());
    }

    public BlockState rotate(BlockState p_54540_, Rotation p_54541_) {
        return p_54540_.setValue(FACING, p_54541_.rotate(p_54540_.getValue(FACING)));
    }

    public BlockState mirror(BlockState p_54537_, Mirror p_54538_) {
        return p_54537_.rotate(p_54538_.getRotation(p_54537_.getValue(FACING)));
    }

    public RenderShape getRenderShape(BlockState p_54559_) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new KeyboardBlockEntity(pos, state);
    }
}
