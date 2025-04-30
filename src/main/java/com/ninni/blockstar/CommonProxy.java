package com.ninni.blockstar;

import com.ninni.blockstar.server.data.SoundfontManager;
import com.ninni.blockstar.server.packet.PlaySoundPacket;
import com.ninni.blockstar.server.packet.StopSoundPacket;

public class CommonProxy {
    private final SoundfontManager soundfontManager = new SoundfontManager();

    public void init() {
    }

    public void commonSetup() {
    }

    public void clientSetup() {
    }

    public void handlePlaySoundPacket(PlaySoundPacket msg) {
    }

    public void handleStopSoundPacket(StopSoundPacket msg) {
    }

    public boolean isScreenShiftDown() {
    }

    public SoundfontManager getSoundfontManager() {
        return soundfontManager;
    }
}
