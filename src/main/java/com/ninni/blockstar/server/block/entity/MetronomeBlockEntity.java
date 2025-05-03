package com.ninni.blockstar.server.block.entity;

import com.ninni.blockstar.registry.BBlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MetronomeBlockEntity extends BlockEntity {
    public MetronomeBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BBlockEntityRegistry.METRONOME.get(), blockPos, blockState);
    }
}
