package com.ninni.blockstar.client.sound;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class SoundfontSound extends AbstractTickableSoundInstance {
    private final ResourceLocation soundLocation;

    public SoundfontSound(ResourceLocation soundLocation, float volume, float pitch, LocalPlayer player) {
        super(SoundEvent.createVariableRangeEvent(soundLocation), SoundSource.RECORDS, player.getRandom());
        this.soundLocation = soundLocation;
        this.volume = volume;
        this.pitch = pitch;
        this.looping = false;

        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();
    }

    @Override
    public void tick() {
    }

    @Override
    public ResourceLocation getLocation() {
        return this.soundLocation;
    }
}