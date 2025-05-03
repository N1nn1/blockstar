package com.ninni.blockstar.server.block.entity;

import com.ninni.blockstar.registry.BBlockEntityRegistry;
import com.ninni.blockstar.registry.BSoundEventRegistry;
import com.ninni.blockstar.server.block.MetronomeBlock;
import com.ninni.blockstar.server.block.RodType;
import com.ninni.blockstar.server.item.MetronomeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public class MetronomeBlockEntity extends BlockEntity {
    private int beatCounter = 0;
    private int bpm = 100;
    private String timeSig = "4/4";
    private UUID uuid;
    private boolean pulsing = false;
    private boolean ticking;
    private int pulseTicksRemaining = 0;
    private int currentSignalStrength = 0;
    private long lastTickTime = 0;

    public MetronomeBlockEntity(BlockPos pos, BlockState state) {
        super(BBlockEntityRegistry.METRONOME.get(), pos, state);
    }

    public void tickServer() {
        ticking = level != null && !level.isClientSide && bpm > 0 && level.getBlockState(worldPosition).getValue(MetronomeBlock.POWERED) != level.getBlockState(worldPosition).getValue(MetronomeBlock.INVERTED);

        if (ticking) {
            int intervalTicks = 1200 / bpm;
            long gameTime = level.getGameTime();

            if (gameTime - lastTickTime >= intervalTicks) {
                lastTickTime = gameTime;

                int beatsPerMeasure = MetronomeItem.getTimeSigValues(timeSig, true);
                RodType rod = (beatCounter % 2 == 0) ? RodType.LEFT : RodType.RIGHT;

                boolean isDownbeat = (beatCounter % beatsPerMeasure) == 0;
                beatCounter++;

                pulsing = true;
                pulseTicksRemaining = 2;
                currentSignalStrength = isDownbeat ? 15 : 4;
                level.updateNeighborsAt(worldPosition, getBlockState().getBlock());

                level.setBlock(worldPosition, getBlockState().setValue(MetronomeBlock.ROD, rod), 3);
                level.playSound(null, worldPosition, isDownbeat ? BSoundEventRegistry.METRONOME_DOWNBEAT.get() : BSoundEventRegistry.METRONOME_BEAT.get(), SoundSource.BLOCKS, 0.8F, 1.0F);
            }

            if (pulsing) {
                pulseTicksRemaining--;
                if (pulseTicksRemaining <= 0) {
                    pulsing = false;
                    currentSignalStrength = 0;
                    level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
                }
            }
        }
    }

    public int getSignalStrength() {
        return pulsing ? currentSignalStrength : 0;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("BPM")) this.bpm = tag.getInt("BPM");
        if (tag.contains("TimeSig")) this.timeSig = tag.getString("TimeSig");
        if (tag.hasUUID("UUID")) this.uuid = tag.getUUID("UUID");
        else this.uuid = UUID.randomUUID();
        this.ticking = tag.getBoolean("Ticking");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("BPM", bpm);
        tag.putString("TimeSig", timeSig);
        tag.putBoolean("Ticking", ticking);
        if (uuid != null) {
            tag.putUUID("UUID", uuid);
        }
    }
}
