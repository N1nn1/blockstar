package com.ninni.blockstar.server.block;

import com.ninni.blockstar.registry.BBlockEntityRegistry;
import com.ninni.blockstar.server.block.entity.ComposingTableBlockEntity;
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
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ComposingTableBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty HAS_PAPER = BooleanProperty.create("paper");
    protected static final VoxelShape SHAPE =  Shapes.or(Block.box(0, 0, 0, 16, 2, 16), Block.box(4, 2, 4, 12, 13, 12));
    protected static final VoxelShape SHAPE_NORTH = Shapes.or(
            Block.box(0, 8, 0, 16, 12, 5),
            Block.box(0, 9, 3, 16, 13, 8),
            Block.box(0, 10, 6, 16, 14, 11),
            Block.box(0, 11, 8, 16, 15, 13),
            Block.box(0, 12, 11, 16, 16, 16),
            SHAPE
    );
    protected static final VoxelShape SHAPE_WEST = Shapes.or(
            Block.box(0, 8, 0, 5, 12, 16),
            Block.box(3, 9, 0, 8, 13, 16),
            Block.box(6, 10, 0, 11, 14, 16),
            Block.box(8, 11, 0, 13, 15, 16),
            Block.box(11, 12, 0, 16, 16, 16),
            SHAPE
    );
    protected static final VoxelShape SHAPE_EAST = Shapes.or(
            Block.box(11, 8, 0, 16, 12, 16),
            Block.box(8, 9, 0, 13, 13, 16),
            Block.box(5, 10, 0, 10, 14, 16),
            Block.box(3, 11, 0, 8, 15, 16),
            Block.box(0, 12, 0, 5, 16, 16),
            SHAPE
    );
    protected static final VoxelShape SHAPE_SOUTH = Shapes.or(
            Block.box(0, 8, 11, 16, 12, 16),
            Block.box(0, 9, 8, 16, 13, 13),
            Block.box(0, 10, 5, 16, 14, 10),
            Block.box(0, 11, 3, 16, 15, 8),
            Block.box(0, 12, 0, 16, 16, 5),
            SHAPE
    );

    public ComposingTableBlock() {
        super(Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false).setValue(HAS_PAPER, false).setValue(FACING, Direction.NORTH));
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof ComposingTableBlockEntity composingTableBlockEntity) {
                player.openMenu(composingTableBlockEntity);
                //player.awardStat(Stats.INTERACT_WITH_FURNACE);
            }
            return InteractionResult.CONSUME;
        }
    }

    public VoxelShape getShape(BlockState p_54561_, BlockGetter p_54562_, BlockPos p_54563_, CollisionContext p_54564_) {
        return switch (p_54561_.getValue(FACING)) {
            case NORTH -> SHAPE_NORTH;
            case SOUTH -> SHAPE_SOUTH;
            case EAST -> SHAPE_EAST;
            case WEST -> SHAPE_WEST;
            default -> SHAPE;
        };
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, BBlockEntityRegistry.COMPOSING_TABLE.get(), ComposingTableBlockEntity::tick);
    }

    public void setPlacedBy(Level p_52676_, BlockPos p_52677_, BlockState p_52678_, LivingEntity p_52679_, ItemStack p_52680_) {
        if (p_52680_.hasCustomHoverName()) {
            BlockEntity blockentity = p_52676_.getBlockEntity(p_52677_);
            if (blockentity instanceof ComposingTableBlockEntity blockEntity) {
                blockEntity.setCustomName(p_52680_.getHoverName());
            }
        }
    }

    public void onRemove(BlockState blockState, Level level, BlockPos pos, BlockState state, boolean b) {
        if (!blockState.is(state.getBlock())) {
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof ComposingTableBlockEntity blockEntity) {
                Containers.dropContents(level, pos, blockEntity);
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
        return super.updateShape(state, direction, state1, levelAccessor, pos, pos1);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_56388_) {
        p_56388_.add(WATERLOGGED, FACING, HAS_PAPER);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockPos blockpos = ctx.getClickedPos();
        FluidState fluidstate = ctx.getLevel().getFluidState(blockpos);
        return this.defaultBlockState().setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER).setValue(FACING, ctx.getHorizontalDirection().getOpposite());
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
        return new ComposingTableBlockEntity(pos, state);
    }
}
