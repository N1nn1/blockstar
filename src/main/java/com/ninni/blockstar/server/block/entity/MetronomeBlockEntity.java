package com.ninni.blockstar.server.block.entity;

import com.ninni.blockstar.registry.BBlockEntityRegistry;
import com.ninni.blockstar.server.block.MetronomeBlock;
import com.ninni.blockstar.server.block.RodType;
import com.ninni.blockstar.server.item.MetronomeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MetronomeBlockEntity extends BlockEntity {
    private long lastTickTime = 0;
    private int beatCounter = 0;
    private int bpm = 100;
    private String timeSig = "4/4";
    private boolean swingPhase = true;
    private boolean pulsing = false;
    private int pulseTicksRemaining = 0;
    private int currentSignalStrength = 0;

    public MetronomeBlockEntity(BlockPos pos, BlockState state) {
        super(BBlockEntityRegistry.METRONOME.get(), pos, state);
    }

    public void tickServer() {
        if (level == null || level.isClientSide || bpm <= 0 || level.getBlockState(worldPosition).getValue(MetronomeBlock.POWERED) == level.getBlockState(worldPosition).getValue(MetronomeBlock.INVERTED)) return;

        int beatsPerMeasure = MetronomeItem.getTimeSigValues(timeSig, true);
        long interval = 60000L / bpm / 2;

        if (level.getGameTime() - lastTickTime >= interval / 50) {
            lastTickTime = level.getGameTime();

            RodType rod;
            if (!swingPhase) rod = RodType.MIDDLE;
            else rod = (beatCounter % 2 == 0) ? RodType.LEFT : RodType.RIGHT;

            swingPhase = !swingPhase;
            if (!swingPhase) {
                beatCounter++;
                boolean isDownbeat = (beatCounter % beatsPerMeasure) == 0;
                pulsing = true;
                pulseTicksRemaining = 2;

                if (isDownbeat) currentSignalStrength = 15;
                else currentSignalStrength = 4;

                level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
            }

            level.setBlock(worldPosition, getBlockState().setValue(MetronomeBlock.ROD, rod), 3);

            if (rod != RodType.MIDDLE) {
                boolean isDownbeat = (beatCounter % beatsPerMeasure) == 0;
                level.playSound(null, worldPosition,
                        isDownbeat ? SoundEvents.NOTE_BLOCK_BASEDRUM.value() : SoundEvents.NOTE_BLOCK_HAT.value(),
                        SoundSource.BLOCKS, 0.8F, 1.0F);
            }
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

    public int getSignalStrength() {
        return pulsing ? currentSignalStrength : 0;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("BPM")) this.bpm = tag.getInt("BPM");
        if (tag.contains("TimeSig")) this.timeSig = tag.getString("TimeSig");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("BPM", bpm);
        tag.putString("TimeSig", timeSig);
    }
}

