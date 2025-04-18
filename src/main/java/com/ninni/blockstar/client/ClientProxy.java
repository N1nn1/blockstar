package com.ninni.blockstar.client;

import com.ninni.blockstar.CommonProxy;
import com.ninni.blockstar.client.event.ClientEvents;
import com.ninni.blockstar.client.gui.KeyboardScreen;
import com.ninni.blockstar.registry.BMenuRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {

    @Override
    public void init() {
        super.init();
        MinecraftForge.EVENT_BUS.register(new ClientEvents());
    }

    @Override
    public void clientSetup() {
        MenuScreens.register(BMenuRegistry.KEYBOARD.get(), KeyboardScreen::new);
    }

}
