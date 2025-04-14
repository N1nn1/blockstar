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
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class KeyboardBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty BOTTOM = BooleanProperty.create("bottom");
    public static final EnumProperty<KeyboardType> TYPE = EnumProperty.create("type", KeyboardType.class);
    protected static final VoxelShape BOTTOM_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
    protected static final VoxelShape TOP_SHAPE = Block.box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape STAND_SHAPE = Block.box(3, 0, 3, 13, 8, 13);
    public static final VoxelShape TOP_STAND_SHAPE = Shapes.or(TOP_SHAPE, STAND_SHAPE);

    public KeyboardBlock() {
        super(Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false).setValue(BOTTOM, true).setValue(TYPE, KeyboardType.SINGLE).setValue(FACING, Direction.NORTH));
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

    public BlockState updateShape(BlockState state, Direction direction, BlockState state1, LevelAccessor levelAccessor, BlockPos pos, BlockPos pos1) {
        if (state.getValue(WATERLOGGED)) {
            levelAccessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        }
        if (state != getConnectedShape(levelAccessor, state, pos)) levelAccessor.setBlock(pos, getConnectedShape(levelAccessor, state, pos), 3);

        return super.updateShape(state, direction, state1, levelAccessor, pos, pos1);
    }

    public BlockState getConnectedShape(LevelAccessor levelAccessor, BlockState state, BlockPos pos) {
        BlockState rightState = levelAccessor.getBlockState(pos.relative(state.getValue(FACING).getCounterClockWise(), 1));
        BlockState leftState = levelAccessor.getBlockState(pos.relative(state.getValue(FACING).getClockWise(), 1));

        if (leftState.getBlock() instanceof KeyboardBlock || rightState.getBlock() instanceof KeyboardBlock) {

            boolean isLeftBottom = leftState.getBlock() instanceof KeyboardBlock && leftState.getValue(BOTTOM) == state.getValue(BOTTOM) && leftState.getValue(FACING) == state.getValue(FACING);
            boolean isRightBottom = rightState.getBlock() instanceof KeyboardBlock && rightState.getValue(BOTTOM) == state.getValue(BOTTOM) && rightState.getValue(FACING) == state.getValue(FACING);

            if (!isRightBottom && isLeftBottom) {
                return state.setValue(TYPE, KeyboardType.RIGHT);
            }
            else if (!isLeftBottom && isRightBottom) {
                return state.setValue(TYPE, KeyboardType.LEFT);
            }
            else if (isLeftBottom && isRightBottom) return state.setValue(TYPE, KeyboardType.MIDDLE);
        }
        return state.setValue(TYPE, KeyboardType.SINGLE);
    }

    public VoxelShape getShape(BlockState state, BlockGetter p_56391_, BlockPos p_56392_, CollisionContext p_56393_) {
        return state.getValue(BOTTOM) ? BOTTOM_SHAPE : state.getValue(TYPE) != KeyboardType.MIDDLE ? TOP_STAND_SHAPE : TOP_SHAPE;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_56388_) {
        p_56388_.add(WATERLOGGED, FACING, BOTTOM, TYPE);
    }


    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockPos blockpos = ctx.getClickedPos();

        FluidState fluidstate = ctx.getLevel().getFluidState(blockpos);
        Direction direction = ctx.getClickedFace();
        BlockState blockstate1 = this.defaultBlockState().setValue(BOTTOM, true).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER).setValue(FACING, ctx.getHorizontalDirection().getOpposite());
        return direction != Direction.DOWN && (direction == Direction.UP || !(ctx.getClickLocation().y - (double)blockpos.getY() > 0.5D)) ? blockstate1 : blockstate1.setValue(BOTTOM, false);
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
