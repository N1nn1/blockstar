package com.ninni.blockstar.client.sound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SoundManagerHelper {
    private static final Map<Integer, Map<Integer, List<SoundfontSound>>> activeSounds = new HashMap<>();

    public static void register(SoundfontSound sound) {
        int userId = sound.getUser().getId();
        activeSounds
                .computeIfAbsent(sound.getNote(), k -> new HashMap<>())
                .computeIfAbsent(userId, k -> new ArrayList<>())
                .add(sound);
    }

    public static void unregister(SoundfontSound sound) {
        int userId = sound.getUser().getId();
        Map<Integer, List<SoundfontSound>> userMap = activeSounds.get(sound.getNote());
        if (userMap != null) {
            List<SoundfontSound> sounds = userMap.get(userId);
            if (sounds != null) {
                sounds.remove(sound);
                if (sounds.isEmpty()) {
                    userMap.remove(userId);
                    if (userMap.isEmpty()) {
                        activeSounds.remove(sound.getNote());
                    }
                }
            }
        }
    }

    public static void releaseMatchingSound(int note, int userId, int releaseTicks) {
        Map<Integer, List<SoundfontSound>> userMap = activeSounds.get(note);
        if (userMap != null) {
            List<SoundfontSound> sounds = userMap.get(userId);
            if (sounds != null) {
                for (SoundfontSound sound : sounds) {
                    sound.startRelease(releaseTicks);
                }
            }
        }
    }

    public static Map<Integer, Map<Integer, List<SoundfontSound>>> getActiveSounds() {
        return activeSounds;
    }
}
