package com.ninni.blockstar.mixin;

import com.ninni.blockstar.server.block.KeyboardBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow protected abstract BlockPos getPrimaryStepSoundBlockPos(BlockPos p_278049_);
    @Shadow public abstract Level level();

    @Inject(at = @At("HEAD"), method = "playStepSound")
    private void B$playStepSound(BlockPos pos, BlockState state, CallbackInfo ci) {
        BlockPos blockpos = this.getPrimaryStepSoundBlockPos(pos);
        BlockState blockstate = this.level().getBlockState(blockpos);
        if (blockstate.getBlock() instanceof KeyboardBlock block) {
            block.getInstrumentType().playNoteSoundFromBlock(blockpos, this.level(), (Entity)(Object)this);
        }
    }

}
