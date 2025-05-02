package com.ninni.blockstar.server.instrument;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.server.data.SoundfontManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class InstrumentType {
    private int lowestNote;
    private int highestNote;
    public int[] major               = {0, 2, 4, 5, 7, 9, 11, 12};
    public int[] minor               = {0, 2, 3, 5, 7, 8, 10, 12};
    public int[] pentatonic          = {0, 3, 5, 7, 10, 12};
    public int[] dorian              = {0, 2, 3, 5, 7, 9, 10, 12};
    public int[] phrygian            = {0, 1, 3, 5, 7, 8, 10, 12};
    public int[] lydian              = {0, 2, 4, 6, 7, 9, 11, 12};
    public int[] mixolydian          = {0, 2, 4, 5, 7, 9, 10, 12};
    public int[] blues               = {0, 3, 5, 6, 7, 10, 12};
    public int[] harmonicMinor       = {0, 2, 3, 5, 7, 8, 11, 12};
    public int[] chromatic           = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
    public int[] doubleHarmonic      = {0, 1, 4, 5, 7, 8, 11, 12};
    public int[] doubleHarmonicMinor = {0, 1, 3, 5, 7, 8, 11, 12};
    public int[] locrian             = {0, 1, 3, 5, 6, 8, 10, 12};
    public int[] superLocrian        = {0, 1, 3, 4, 6, 8, 10, 12};

    public InstrumentType(int lowestNote, int highestNote) {
        this.lowestNote = lowestNote;
        this.highestNote = highestNote;
    }

    public int getRange() {
        return this.highestNote - this.lowestNote;
    }

    public boolean isInRange(int note) {
        return note <= this.highestNote && note >= this.lowestNote;
    }

    public int getLowestNote() {
        return lowestNote;
    }

    public int getHighestNote() {
        return highestNote;
    }

    public abstract void playNoteSoundFromBlock(BlockPos blockpos, Level level, Entity entity);

    public SoundfontManager.SoundfontDefinition getSoundfont(ItemStack stack) {
        SoundfontManager.SoundfontDefinition soundfontDefinition;

        if (!stack.isEmpty() && this.isValidSoundfontForInstrumentType(stack)) {
            soundfontDefinition = Blockstar.PROXY.getSoundfontManager().get(new ResourceLocation(stack.getTag().getString("Soundfont")));
        }
        else soundfontDefinition = getBaseSoundFont();

        return soundfontDefinition != null ? soundfontDefinition : getBaseSoundFont();
    }

    public boolean isValidSoundfontForInstrumentType(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("Soundfont")) {
            SoundfontManager.SoundfontDefinition data = Blockstar.PROXY.getSoundfontManager().get(new ResourceLocation(stack.getTag().getString("Soundfont")));
            return data.instrumentData().containsKey(this);
        }
        return false;
    }

    public SoundfontManager.SoundfontDefinition getBaseSoundFont() {
        return Blockstar.PROXY.getSoundfontManager().get(new ResourceLocation(Blockstar.MODID, "base"));
    }

    int[] getScaleForPlayer(LivingEntity entity, Level level) {
        if (entity.getHealth() < 2) {
            return harmonicMinor;
        } else if (level.isRaining()) {
            return level.getBiome(entity.blockPosition()).get().getPrecipitationAt(entity.blockPosition()).name().equals("NONE") ? phrygian : dorian;
        } else if (entity.hasEffect(MobEffects.SLOW_FALLING)) {
            return pentatonic;
        } else if (entity.hasEffect(MobEffects.MOVEMENT_SPEED)) {
            return mixolydian;
        } else if (entity.hasEffect(MobEffects.REGENERATION)) {
            return lydian;
        } else if (level.isThundering()) {
            return blues;
        } else if (level.dimension() != Level.OVERWORLD) {
            return chromatic;
        } else {
            float temperature = level.getBiome(entity.blockPosition()).value().getBaseTemperature();
            boolean nightTime = level.getDayTime() % 24000 >= 13000;

            if (temperature >= 1.0F) {
                return nightTime ? doubleHarmonicMinor : doubleHarmonic;
            } else if (temperature <= 0.2F) {
                return nightTime ? superLocrian : locrian;
            } else {
                return nightTime ? minor : major;
            }
        }
    }

}
