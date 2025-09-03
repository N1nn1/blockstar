package com.ninni.blockstar;

import com.ninni.blockstar.server.data.SoundfontManager;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;

public class CommonProxy {
    private final SoundfontManager soundfontManager = new SoundfontManager();

    public void init() {
    }

    public void commonSetup() {
    }

    public void clientSetup() {
    }

    public Level getWorld() {
        return ServerLifecycleHooks.getCurrentServer().overworld();
    }

    public SoundfontManager getSoundfontManager() {
        return soundfontManager;
    }
}
