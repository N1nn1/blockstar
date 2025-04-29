package com.ninni.blockstar.server.instrument;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.server.data.SoundfontManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

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
}
