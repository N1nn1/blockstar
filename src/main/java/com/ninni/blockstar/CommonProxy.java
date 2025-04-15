package com.ninni.blockstar;

import com.ninni.blockstar.server.event.CommonEvents;
import net.minecraftforge.common.MinecraftForge;

public class CommonProxy {

    public void init() {
        MinecraftForge.EVENT_BUS.register(new CommonEvents());
    }

    public void commonSetup() {
    }

    public void clientSetup() {
    }
}
