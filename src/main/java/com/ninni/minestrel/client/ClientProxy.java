package com.ninni.minestrel.client;

import com.ninni.minestrel.CommonProxy;
import com.ninni.minestrel.client.event.ClientEventHandler;
import com.ninni.minestrel.client.gui.KeyboardScreen;
import com.ninni.minestrel.registry.MMenuRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {

    @Override
    public void init() {
        super.init();
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
    }

    @Override
    public void clientSetup() {
        MenuScreens.register(MMenuRegistry.KEYBOARD.get(), KeyboardScreen::new);
    }
}
