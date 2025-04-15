package com.ninni.blockstar.server.event;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.server.data.SoundfontManager;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Blockstar.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonEvents {
    public static final SoundfontManager SOUNDFONTS = new SoundfontManager();

    @SubscribeEvent
    public void register(AddReloadListenerEvent event) {
        event.addListener(SOUNDFONTS);
    }
}
