package com.ninni.blockstar;

import com.ninni.blockstar.server.data.SoundfontManager;
import com.ninni.blockstar.server.midi.MidiInputHandler;

public class CommonProxy {
    private final SoundfontManager soundfontManager = new SoundfontManager();

    public void init() {
        MidiInputHandler.startListening();
    }

    public void commonSetup() {
    }

    public void clientSetup() {
    }

    public SoundfontManager getSoundfontManager() {
        return soundfontManager;
    }
}
