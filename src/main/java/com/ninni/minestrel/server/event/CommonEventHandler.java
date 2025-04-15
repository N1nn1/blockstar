package com.ninni.minestrel.server.event;

import com.ninni.minestrel.Blockstar;
import com.ninni.minestrel.server.data.SoundfontManager;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Blockstar.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonEventHandler {
    public static final SoundfontManager SOUNDFONTS = new SoundfontManager();

    @SubscribeEvent
    public void register(AddReloadListenerEvent event) {
        event.addListener(SOUNDFONTS);
    }
}
