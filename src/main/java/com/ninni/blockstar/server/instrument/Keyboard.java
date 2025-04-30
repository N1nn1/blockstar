package com.ninni.blockstar.server.instrument;

import com.ninni.blockstar.registry.BInstrumentTypeRegistry;
import com.ninni.blockstar.registry.BNetwork;
import com.ninni.blockstar.server.block.KeyboardBlock;
import com.ninni.blockstar.server.block.entity.KeyboardBlockEntity;
import com.ninni.blockstar.server.data.SoundfontManager;
import com.ninni.blockstar.server.packet.PlaySoundPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;

public class Keyboard extends InstrumentType {
    public Keyboard() {
        super(48, 76);
    }

    @Override
    public void playNoteSoundFromBlock(BlockPos blockpos, Level level, Entity entity) {

        if (entity instanceof LivingEntity livingEntity && level.getBlockEntity(blockpos) instanceof KeyboardBlockEntity blockEntity && level.getBlockState(blockpos).getBlock() instanceof KeyboardBlock) {

            int[] scale = getScaleForPlayer(livingEntity, level);

            int scaleIndex = Math.floorMod(blockpos.getX() + blockpos.getZ(), scale.length);
            int semitoneOffset = scale[scaleIndex];
            int note = 54 + semitoneOffset;

            int noteIndex = semitoneOffset % 25;
            float particleColor = noteIndex / 24.0f;

            SoundfontManager.SoundfontDefinition soundfont = this.getSoundfont(blockEntity.getItem(0));

            if (soundfont != null && level instanceof ServerLevel serverLevel) {
                SoundfontManager.InstrumentSoundfontData forInstrument = soundfont.getForInstrument(this);
                int sampleNote = forInstrument.getClosestSampleNote(note);
                float pitch = (float) Math.pow(2, (note - sampleNote) / 12.0);

                String velocity = forInstrument.velocityLayers().isPresent() ? "_" + forInstrument.velocityLayers().get() : "";
                ResourceLocation resourceLocation = new ResourceLocation(soundfont.name().getNamespace(), "soundfont." + BInstrumentTypeRegistry.get(this).getPath() + "." + soundfont.name().getPath() + "." + sampleNote + velocity);

                BNetwork.INSTANCE.send(
                        PacketDistributor.NEAR.with(() -> PacketDistributor.TargetPoint.p(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), 32, livingEntity.level().dimension()).get()),
                        new PlaySoundPacket(resourceLocation, pitch, livingEntity.getId(), note)
                );

                if (livingEntity instanceof ServerPlayer player) {
                    serverLevel.sendParticles(
                            player,
                            ParticleTypes.NOTE,
                            true,
                            blockpos.getX() + 0.5D,
                            blockpos.getY() + 1.0D,
                            blockpos.getZ() + 0.5D,
                            0,
                            particleColor, 0.0D, 0.0D,
                            1.0D
                    );
                }
            }

        }
    }
}
