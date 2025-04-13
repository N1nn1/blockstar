package com.ninni.minestrel.server.block.entity;

import com.ninni.minestrel.registry.MBlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class KeyboardBlockEntity extends BlockEntity {
    public KeyboardBlockEntity(BlockPos pos, BlockState state) {
        super(MBlockEntityRegistry.KEYBOARD.get(), pos, state);
    }
}
