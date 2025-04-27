package com.ninni.blockstar.server.event;

import com.ninni.blockstar.Blockstar;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Blockstar.MODID, value = Dist.DEDICATED_SERVER)
public class CommonEvents {

    @SubscribeEvent
    public void register(AddReloadListenerEvent event) {
        event.addListener(Blockstar.PROXY.getSoundfontManager());
    }
}
