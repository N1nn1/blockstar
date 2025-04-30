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

import java.awt.*;

public abstract class InstrumentType {
    private int lowestNote;
    private int highestNote;

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

        int[] major          = {0, 2, 4, 5, 7, 9, 11, 12};
        int[] minor          = {0, 2, 3, 5, 7, 8, 10, 12};
        int[] pentatonic     = {0, 3, 5, 7, 10, 12};
        int[] dorian         = {0, 2, 3, 5, 7, 9, 10, 12};
        int[] phrygian       = {0, 1, 3, 5, 7, 8, 10, 12};
        int[] lydian         = {0, 2, 4, 6, 7, 9, 11, 12};
        int[] mixolydian     = {0, 2, 4, 5, 7, 9, 10, 12};
        int[] blues          = {0, 3, 5, 6, 7, 10, 12};
        int[] harmonicMinor  = {0, 2, 3, 5, 7, 8, 11, 12};
        int[] chromatic      = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};

        if (entity.isOnFire()) {
            return phrygian;
        } else if (entity.getHealth() < 2) {
            return minor;
        } else if (entity.isInWater()) {
            return dorian;
        } else if (entity.hasEffect(MobEffects.SLOW_FALLING)) {
            return pentatonic;
        } else if (entity.hasEffect(MobEffects.MOVEMENT_SPEED)) {
            return mixolydian;
        } else if (entity.hasEffect(MobEffects.REGENERATION)) {
            return lydian;
        } else if (level.isThundering()) {
            return blues;
        } else if (level.getDayTime() % 24000 >= 13000) {
            return harmonicMinor;
        } else if (level.dimension() == Level.END) {
            return chromatic;
        } else {
            return major;
        }
    }

}
