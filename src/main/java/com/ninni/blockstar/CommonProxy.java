package com.ninni.blockstar;

import com.ninni.blockstar.server.midi.MidiInputHandler;
import com.ninni.blockstar.server.data.SoundfontManager;
import com.ninni.blockstar.server.packet.PlaySoundPacket;

public class CommonProxy {
    private final SoundfontManager soundfontManager = new SoundfontManager();

    public void init() {
        MidiInputHandler.startListening();
    }

    public void commonSetup() {
    }

    public void clientSetup() {
    }

    public void handlePlaySoundPacket(PlaySoundPacket msg) {
    }

    public SoundfontManager getSoundfontManager() {
        return soundfontManager;
    }
}
