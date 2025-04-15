package com.ninni.minestrel;

import com.ninni.minestrel.server.event.CommonEventHandler;
import net.minecraftforge.common.MinecraftForge;

public class CommonProxy {

    public void init() {
        MinecraftForge.EVENT_BUS.register(new CommonEventHandler());
    }

    public void commonSetup() {
    }

    public void clientSetup() {
    }
}
