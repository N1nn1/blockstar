package com.ninni.blockstar.server.intstrument;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.client.sound.SoundfontSound;
import com.ninni.blockstar.registry.BInstrumentTypeRegistry;
import com.ninni.blockstar.server.data.SoundfontManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

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

    public @NotNull SoundfontSound getSoundfontSound(SoundfontManager.SoundfontDefinition soundfont, int note, LivingEntity entity) {
        int sampleNote = soundfont.getForInstrument(this).getClosestSampleNote(note);
        float pitch = (float) Math.pow(2, (note - sampleNote) / 12.0);

        String velocity = soundfont.getForInstrument(this).velocityLayers().isPresent() ? "_" + 2 : "";
        ResourceLocation resourceLocation = new ResourceLocation(soundfont.name().getNamespace(), "soundfont." + BInstrumentTypeRegistry.get(this).getPath() + "." + soundfont.name().getPath() + "." + sampleNote + velocity);
        return new SoundfontSound(resourceLocation, 1.0f, pitch, entity);
    }

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
