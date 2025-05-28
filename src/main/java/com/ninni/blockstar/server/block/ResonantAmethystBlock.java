package com.ninni.blockstar.server.block;

import com.ninni.blockstar.registry.BBlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.Nullable;

public class ResonantAmethystBlock extends BuddingAmethystBlock {
    public static final BooleanProperty BUDDING = BooleanProperty.create("budding");
    public static final IntegerProperty DISTANCE = IntegerProperty.create("distance", 0,4);

    public ResonantAmethystBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(1.5F).randomTicks().sound(SoundType.AMETHYST).requiresCorrectToolForDrops());
        this.registerDefaultState(this.defaultBlockState().setValue(BUDDING, false).setValue(DISTANCE, 4));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return updateDistanceFromSource(this.defaultBlockState(), ctx.getLevel(), ctx.getClickedPos());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        int currentDistance = state.getValue(DISTANCE);
        int newDistance = getDistanceFromSource(neighborState) + 1;

        if (newDistance != 1 || currentDistance != newDistance) {
            level.scheduleTick(pos, this, 15 + level.getRandom().nextInt(35));
        }

        return state;
    }

    @Override
    public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        serverLevel.setBlock(blockPos, updateDistanceFromSource(blockState, serverLevel, blockPos), Block.UPDATE_ALL);
    }

    private static BlockState updateDistanceFromSource(BlockState state, Level world, BlockPos pos) {
        int newDistance = 4;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (Direction direction : Direction.values()) {
            mutable.setWithOffset(pos, direction);
            newDistance = Math.min(newDistance, getDistanceFromSource(world.getBlockState(mutable)) + 1);
            if (newDistance == 1) break;
        }

        boolean wasBudding = state.getValue(BUDDING);
        boolean nowBudding = newDistance < 4;

        if (wasBudding != nowBudding) {
            SoundType soundType = state.getSoundType();
            float pitch = nowBudding ? 1F + (newDistance * 0.1F) : 1F - (newDistance * 0.1F);

            world.playSound(null, pos, nowBudding ? soundType.getPlaceSound() : soundType.getBreakSound(), SoundSource.BLOCKS, 0.75F, pitch);
        }

        return state.setValue(DISTANCE, newDistance).setValue(BUDDING, nowBudding);
    }



    public static int getDistanceFromSource(BlockState state) {
        if (state.is(Blocks.BUDDING_AMETHYST)) {
            return 0;
        }
        if (state.is(BBlockRegistry.RESONANT_AMETHYST_BLOCK.get()) && state.getValue(BUDDING)) {
            return state.getValue(DISTANCE);
        }
        return 4;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        if (state.getValue(BUDDING)) {
            super.randomTick(state, serverLevel, blockPos, randomSource);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_56388_) {
        p_56388_.add(BUDDING, DISTANCE);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(BUDDING) && super.isRandomlyTicking(state);
    }
}
