package com.ninni.blockstar.client.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;

public class SoundfontSound extends AbstractTickableSoundInstance {
    private final ResourceLocation soundLocation;
    private final LivingEntity user;
    private boolean fadingOut = false;
    private int fadeOutTicks = 0;
    private final float initialVolume;

    public SoundfontSound(ResourceLocation soundLocation, float volume, float pitch, LivingEntity user) {
        super(SoundEvent.createVariableRangeEvent(soundLocation), SoundSource.RECORDS, user.getRandom());
        this.soundLocation = soundLocation;
        this.volume = volume;
        this.initialVolume = volume;
        this.user = user;
        this.pitch = pitch;
        this.looping = false;

        this.x = user.getX();
        this.y = user.getY();
        this.z = user.getZ();

        SoundManagerHelper.register(this);
    }

    @Override
    public void tick() {
        if (this.fadingOut) {
            if (fadeOutTicks > 0) {
                fadeOutTicks--;
                this.volume = initialVolume * ((float) fadeOutTicks / 20.0f);
            } else {
                this.stop();
                SoundManagerHelper.unregister(this);
            }
        }
    }

    public void startFadeOut(int ticks) {
        if (ticks > 0) {
            if (!this.fadingOut) {
                this.fadingOut = true;
                this.fadeOutTicks = ticks;
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

    public LivingEntity getUser() {
        return user;
    }
}
