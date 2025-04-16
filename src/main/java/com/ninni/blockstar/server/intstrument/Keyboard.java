package com.ninni.blockstar.server.intstrument;

import com.ninni.blockstar.registry.BInstrumentTypeRegistry;
import com.ninni.blockstar.server.block.KeyboardBlock;
import com.ninni.blockstar.server.block.entity.KeyboardBlockEntity;
import com.ninni.blockstar.server.data.SoundfontManager;
import com.ninni.blockstar.server.packet.BNetworking;
import com.ninni.blockstar.server.packet.ServerPlayNoteSoundPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class Keyboard extends InstrumentType {
    public Keyboard() {
        super(48, 76);
    }

    @Override
    public void playNoteSoundFromBlock(BlockPos blockpos, Level level) {
        if (level.getBlockEntity(blockpos) instanceof KeyboardBlockEntity blockEntity && level.getBlockState(blockpos).getBlock() instanceof KeyboardBlock keyboardBlock) {
            int note = ((blockpos.getX() + blockpos.getZ()) % 13);
            SoundfontManager.SoundfontDefinition soundfont = this.getSoundfont(blockEntity.getItem(0));
            if (soundfont != null && !level.isClientSide) {
                BNetworking.sendToAllNear(blockpos, 32, level, new ServerPlayNoteSoundPacket(blockpos, note + 54, soundfont.name(), keyboardBlock.getInstrumentType()));
            }

            level.addParticle(ParticleTypes.NOTE,
                    blockpos.getX() + 0.5D,
                    blockpos.getY() + 0.7D,
                    blockpos.getZ() + 0.5D,
                    (double)note / 24, 0.0D, 0.0D
            );
        }
    }

    @Override
    public void playNoteSound(BlockPos blockpos, Level level, int note) {

        //if (level.getBlockEntity(blockpos) instanceof KeyboardBlockEntity blockEntity && level.getBlockState(blockpos).getBlock() instanceof KeyboardBlock keyboardBlock) {
        //    SoundfontManager.SoundfontDefinition soundfont = this.getSoundfont(blockEntity.getItem(0));
        //    if (soundfont != null && level instanceof ServerLevel) {
        //        BNetworking.sendToAllNear(blockpos, 32, level, new ServerPlayNoteSoundPacket(blockpos, note, soundfont.name(), keyboardBlock.getInstrumentType()));
        //    }

            //level.addParticle(ParticleTypes.NOTE,
            //        blockpos.getX() + 0.5D,
            //        blockpos.getY() + 0.7D,
            //        blockpos.getZ() + 0.5D,
            //        (double)note / 24, 0.0D, 0.0D
            //);
        //}
    }
}
