package com.ninni.minestrel.server.soundfont;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class SoundfontSound extends AbstractTickableSoundInstance {
    private final ResourceLocation soundLocation;
    private final int note;

    public SoundfontSound(ResourceLocation soundLocation, float volume, float pitch, LocalPlayer player, int note) {
        super(SoundEvent.createVariableRangeEvent(soundLocation), SoundSource.RECORDS, player.getRandom());
        this.soundLocation = soundLocation;
        this.volume = volume;
        this.pitch = pitch;
        this.looping = false;
        this.note = note;

        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();
    }

    @Override
    public void tick() {
    }

    public int getNote() {
        return note;
    }

    @Override
    public ResourceLocation getLocation() {
        return this.soundLocation;
    }
}