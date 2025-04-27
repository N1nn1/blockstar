package com.ninni.blockstar;

import com.ninni.blockstar.server.data.SoundfontManager;

import javax.annotation.Nullable;

public class CommonProxy {
    @Nullable
    private SoundfontManager soundfontManager;

    public void init() {
    }

    public void commonSetup() {
    }

    public void clientSetup() {
    }

    public SoundfontManager getSoundfontManager() {
        if (soundfontManager == null) {
            soundfontManager = new SoundfontManager();
        }
        return soundfontManager;
    }
}
