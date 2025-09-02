package com.ninni.blockstar.server.block;

import com.ninni.blockstar.server.block.entity.MetronomeBlockEntity;
import com.ninni.blockstar.server.item.MetronomeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.checkerframework.checker.nullness.qual.Nullable;

public class MetronomeBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty INVERTED = BlockStateProperties.INVERTED;
    public static final EnumProperty<RodType> ROD = EnumProperty.create("rod", RodType.class);
    protected static final VoxelShape SHAPE_NORTH = Shapes.or(Block.box(2, 5, 11, 14, 14, 14), Block.box(2, 0, 2, 14, 5, 14));
    protected static final VoxelShape SHAPE_SOUTH = Shapes.or(Block.box(2, 5, 2, 14, 14, 5), Block.box(2, 0, 2, 14, 5, 14));
    protected static final VoxelShape SHAPE_WEST = Shapes.join(Block.box(2, 0, 2, 14, 5, 14), Block.box(11, 5, 2, 14, 14, 14), BooleanOp.OR);
    protected static final VoxelShape SHAPE_EAST = Shapes.join(Block.box(2, 0, 2, 14, 5, 14), Block.box(2, 5, 2, 5, 14, 14), BooleanOp.OR);

    public MetronomeBlock() {
        super(Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false).setValue(INVERTED, false).setValue(POWERED, false).setValue(ROD, RodType.MIDDLE).setValue(FACING, Direction.NORTH));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            level.setBlock(pos, state.setValue(INVERTED, !state.getValue(INVERTED)), 3);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        ItemStack stack = new ItemStack(this);
        if (level.getBlockEntity(pos) instanceof MetronomeBlockEntity blockEntity) {
            blockEntity.saveToItem(stack);
        }
        return stack;
    }


    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (level.isClientSide) return;

        Direction inputDirection = state.getValue(FACING);

        if (fromPos.equals(pos.relative(inputDirection))) {
            boolean isPowered = level.hasSignal(fromPos, inputDirection);
            if (isPowered != state.getValue(POWERED)) {
                level.setBlock(pos, state.setValue(POWERED, isPowered), 3);
            }
        }
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : (lvl, pos, st, blockEntity) -> {
            if (blockEntity instanceof MetronomeBlockEntity metronome) metronome.tickServer();
        };
    }

    public void setPlacedBy(Level level, BlockPos blockPos, BlockState state, LivingEntity entity, ItemStack stack) {
        if (stack.getTag() != null && stack.getTag().contains("BlockEntityTag")) {
            CompoundTag blockEntityTag = stack.getTag().getCompound("BlockEntityTag");
            if (MetronomeItem.isTicking(stack)) level.setBlock(blockPos, state.setValue(INVERTED, true), 3);
            level.getBlockEntity(blockPos).load(blockEntityTag);
        }
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        Direction back = state.getValue(FACING);
        if (direction == back) {
            if (level.getBlockEntity(pos) instanceof MetronomeBlockEntity metronome) {
                return metronome.getSignalStrength();
            }
        }
        return 0;
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction side) {
        if (side == null) return false;

        Direction facing = state.getValue(FACING);
        Direction left = facing.getCounterClockWise();
        Direction right = facing.getClockWise();

        return side == left || side == right;
    }

    public boolean hasAnalogOutputSignal(BlockState p_49058_) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return state.getValue(INVERTED) != state.getValue(POWERED) ? 15 : 0;
    }

    public FluidState getFluidState(BlockState p_56397_) {
        return p_56397_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_56397_);
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState state1, LevelAccessor levelAccessor, BlockPos pos, BlockPos pos1) {
        if (state.getValue(WATERLOGGED)) {
            levelAccessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        }
        return super.updateShape(state, direction, state1, levelAccessor, pos, pos1);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_56388_) {
        p_56388_.add(WATERLOGGED, FACING, POWERED, INVERTED, ROD);
    }

    public VoxelShape getShape(BlockState p_54561_, BlockGetter p_54562_, BlockPos p_54563_, CollisionContext p_54564_) {
        return switch (p_54561_.getValue(FACING)) {
            case SOUTH -> SHAPE_SOUTH;
            case EAST -> SHAPE_EAST;
            case WEST -> SHAPE_WEST;
            default -> SHAPE_NORTH;
        };
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        FluidState fluidstate = context.getLevel().getFluidState(blockpos);
        BlockState blockstate = this.defaultBlockState();

        if (context.getLevel().hasNeighborSignal(context.getClickedPos())) {
            blockstate = blockstate.setValue(POWERED, true);
        }
        return blockstate.setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER).setValue(FACING, context.getHorizontalDirection().getOpposite());
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

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MetronomeBlockEntity(pos, state);
    }
}
