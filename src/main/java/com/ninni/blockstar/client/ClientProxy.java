package com.ninni.blockstar.client;

import com.ninni.blockstar.CommonProxy;
import com.ninni.blockstar.client.gui.KeyboardScreen;
import com.ninni.blockstar.registry.BMenuRegistry;
import net.minecraft.client.gui.screens.MenuScreens;

public class ClientProxy extends CommonProxy {

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void clientSetup() {
        MenuScreens.register(BMenuRegistry.KEYBOARD.get(), KeyboardScreen::new);
    }
}
