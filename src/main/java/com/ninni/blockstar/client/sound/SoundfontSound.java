package com.ninni.blockstar.client.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;

public class SoundfontSound extends AbstractTickableSoundInstance {
    private final ResourceLocation soundLocation;
    private final LivingEntity user;
    private boolean released = false;
    private final int note;
    private int releaseTicks = 0;
    private final float initialVolume;

    public SoundfontSound(int note, ResourceLocation soundLocation, float volume, float pitch, LivingEntity user, Optional<Integer> autoFadeTicks) {
        super(SoundEvent.createVariableRangeEvent(soundLocation), SoundSource.RECORDS, user.getRandom());
        this.soundLocation = soundLocation;
        this.note = note;
        this.volume = volume;
        this.initialVolume = volume;
        this.user = user;
        this.pitch = pitch;
        this.looping = false;

        this.x = user.getX();
        this.y = user.getY();
        this.z = user.getZ();

        SoundManagerHelper.register(this);

        if (autoFadeTicks.isPresent()) {
            released = true;
            releaseTicks = autoFadeTicks.get();
        }
    }

    @Override
    public void tick() {
        if (this.released) {
            if (releaseTicks > 0) {
                releaseTicks--;
                this.volume = initialVolume * ((float) releaseTicks / 20.0f);
            } else {
                this.stop();
                SoundManagerHelper.unregister(this);
            }
        }
    }

    public void startRelease(int ticks) {
        if (ticks > 0) {
            if (!this.released) {
                this.released = true;
                this.releaseTicks = ticks;
            }
        } else {
            this.stop();
            SoundManagerHelper.unregister(this);
        }
    }

    @Override
    public ResourceLocation getLocation() {
        return this.soundLocation;
    }

    public int getNote() {
        return note;
    }

    public LivingEntity getUser() {
        return user;
    }
}
