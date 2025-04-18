package com.ninni.blockstar.server.intstrument;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.server.block.KeyboardBlock;
import com.ninni.blockstar.server.block.entity.KeyboardBlockEntity;
import com.ninni.blockstar.server.data.SoundfontManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class Keyboard extends InstrumentType {
    public Keyboard() {
        super(48, 76);
    }

    @Override
    public void playNoteSoundFromBlock(BlockPos blockpos, Level level, Entity entity) {
        if (level.getBlockEntity(blockpos) instanceof KeyboardBlockEntity blockEntity && level.getBlockState(blockpos).getBlock() instanceof KeyboardBlock keyboardBlock) {
            int note = ((blockpos.getX() + blockpos.getZ()) % 13);
            SoundfontManager.SoundfontDefinition soundfont = this.getSoundfont(blockEntity.getItem(0));
            if (soundfont != null) {
                //Blockstar.PROXY.playSpecialSound(entity, entity2 -> getSoundfontSound(soundfont, note + 54, entity2));
            }

            level.addParticle(ParticleTypes.NOTE,
                    blockpos.getX() + 0.5D,
                    blockpos.getY() + 0.7D,
                    blockpos.getZ() + 0.5D,
                    (double)note / 24, 0.0D, 0.0D
            );
        }
    }
}
