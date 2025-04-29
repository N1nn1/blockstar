package com.ninni.blockstar.client.sound;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class SoundManagerHelper {
    private static final List<SoundfontSound> playingSounds = new ArrayList<>();

    public static void register(SoundfontSound sound) {
        playingSounds.add(sound);
    }

    public static void unregister(SoundfontSound sound) {
        playingSounds.remove(sound);
    }

    public static List<SoundfontSound> getPlayingSounds() {
        return playingSounds;
    }

    public static void fadeOutMatchingSound(ResourceLocation soundLoc, float pitch, double x, double y, double z, int fadeTicks) {
        for (SoundfontSound sound : playingSounds) {
            if (sound.getLocation().equals(soundLoc) && Math.abs(sound.getPitch() - pitch) < 0.01f && sound.getUser().distanceToSqr(x, y, z) < 4.0) {
                sound.startFadeOut(fadeTicks);
            }
        }
    }
}
