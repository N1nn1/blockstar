package com.ninni.blockstar.server.sheetmusic;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.util.ArrayList;
import java.util.List;

public class SheetNote {
    public int tick;
    public int pitch;
    public int duration;
    public int velocity;

    public SheetNote(int tick, int pitch, int duration, int velocity) {
        this.tick = tick;
        this.pitch = pitch;
        this.duration = duration;
        this.velocity = velocity;
    }

    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Tick", tick);
        tag.putInt("Pitch", pitch);
        tag.putInt("Duration", duration);
        tag.putInt("Velocity", velocity);
        return tag;
    }

    public static SheetNote fromNBT(CompoundTag tag) {
        return new SheetNote(
                tag.getInt("Tick"),
                tag.getInt("Pitch"),
                tag.getInt("Duration"),
                tag.getInt("Velocity")
        );
    }

    public List<Component> getTooltip() {
        List<Component> noteTooltip = new ArrayList<>();

        noteTooltip.add(Component.literal(this.getNoteName()).withStyle(Style.EMPTY.withColor(this.getNoteColor())));
        noteTooltip.add(Component.literal("Velocity: " + String.valueOf(this.velocity)).withStyle(ChatFormatting.GRAY));
        return noteTooltip;
    }

    public String getNoteName() {
        String[] notes = {"C", "C♯", "D", "D♯", "E", "F", "F♯", "G", "G♯", "A", "A♯", "B"};
        int octave = (pitch / 12) - 1;
        String name = notes[pitch % 12];
        return name + octave;
    }

    public int getNoteColor() {
        int relativePitch = (pitch - 54) % 25;
        if (relativePitch < 0) relativePitch += 25;

        float brightness = ((float) velocity / 127) * 0.75F + 0.25F;

        int rgb = java.awt.Color.HSBtoRGB(relativePitch / 24.0f, 1.0f, brightness);
        return 0xFF000000 | (rgb & 0x00FFFFFF);
    }

    public int getMaxVelocity() {
        return 127;
    }

    public int getMinVelocity() {
        return 1;
    }
}
