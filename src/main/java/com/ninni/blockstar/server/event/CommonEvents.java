package com.ninni.blockstar.server.event;

import com.ninni.blockstar.Blockstar;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CommonEvents {

    @SubscribeEvent
    public void register(AddReloadListenerEvent event) {
        event.addListener(Blockstar.PROXY.getSoundfontManager());
    }
}
