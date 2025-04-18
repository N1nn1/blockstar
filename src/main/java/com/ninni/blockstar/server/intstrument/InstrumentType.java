package com.ninni.blockstar.server.intstrument;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.client.sound.SoundfontSound;
import com.ninni.blockstar.registry.BInstrumentTypeRegistry;
import com.ninni.blockstar.server.data.SoundfontManager;
import com.ninni.blockstar.server.event.CommonEvents;
import net.minecraft.client.Minecraft;
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

    public static @NotNull SoundfontSound getSoundfontSound(SoundfontManager.SoundfontDefinition soundfont, int note, LivingEntity entity) {
        int sampleNote = soundfont.getClosestSampleNote(note);
        float pitch = (float) Math.pow(2, (note - sampleNote) / 12.0);

        String velocity = soundfont.velocityLayers().isPresent() ? "_" + 2 : "";
        ResourceLocation resourceLocation = new ResourceLocation(soundfont.name().getNamespace(), "soundfont." + BInstrumentTypeRegistry.get(soundfont.instrumentType()).getPath() + "." + soundfont.name().getPath() + "." + sampleNote + velocity);
        return new SoundfontSound(resourceLocation, 1.0f, pitch, entity);
    }

    public boolean isValidSoundfontForInstrumentType(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("Soundfont")) {
            return !stack.getTag().contains("InstrumentType") || stack.getTag().getString("InstrumentType").equals(BInstrumentTypeRegistry.get(this).toString());
        }
        return false;
    }

    public SoundfontManager.SoundfontDefinition getSoundfont(ItemStack stack) {
        SoundfontManager.SoundfontDefinition soundfontDefinition = null;
        String path = BInstrumentTypeRegistry.get(this).getPath();

        if (!stack.isEmpty()) {
            if (this.isValidSoundfontForInstrumentType(stack)) {
                ResourceLocation resourceLocation = new ResourceLocation(stack.getTag().getString("Soundfont"));
                soundfontDefinition = Blockstar.PROXY.getSoundfontManager().get(new ResourceLocation(resourceLocation.getNamespace(), path + "/" + resourceLocation.getPath()));
            }
        } else {
            soundfontDefinition = Blockstar.PROXY.getSoundfontManager().get(new ResourceLocation(Blockstar.MODID, path + "/base"));
        }

        if (soundfontDefinition != null) return soundfontDefinition;
        else return Blockstar.PROXY.getSoundfontManager().get(new ResourceLocation(Blockstar.MODID, path + "/base"));
    }
}
