package com.ninni.blockstar.client.sound;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SoundfontSound extends AbstractTickableSoundInstance {
    private final ResourceLocation soundLocation;
    private final LivingEntity user;

    public SoundfontSound(ResourceLocation soundLocation, float volume, float pitch, LivingEntity user) {
        super(SoundEvent.createVariableRangeEvent(soundLocation), SoundSource.RECORDS, user.getRandom());
        this.soundLocation = soundLocation;
        this.volume = volume;
        this.pitch = pitch;
        this.looping = false;
        this.user = user;

        this.x = user.getX();
        this.y = user.getY();
        this.z = user.getZ();
    }

    @Override
    public void tick() {
    }

    @Override
    public ResourceLocation getLocation() {
        return this.soundLocation;
    }

    public boolean isSameEntity(Entity user) {
        return this.user.isAlive() && this.user.getId() == user.getId();
    }
}