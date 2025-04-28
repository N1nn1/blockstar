package com.ninni.blockstar.client;

import com.ninni.blockstar.CommonProxy;
import com.ninni.blockstar.client.config.MidiSettingsConfig;
import com.ninni.blockstar.client.gui.KeyboardScreen;
import com.ninni.blockstar.client.misc.text.ResonantPrismTooltip;
import com.ninni.blockstar.registry.BMenuRegistry;
import com.ninni.blockstar.server.item.ResonantPrismItem;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientProxy extends CommonProxy {

    @Override
    public void init() {
        MidiSettingsConfig.load();
        super.init();

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::registerTooltips);
    }

    @Override
    public void clientSetup() {
        MenuScreens.register(BMenuRegistry.KEYBOARD.get(), KeyboardScreen::new);
    }

    private void registerTooltips(RegisterClientTooltipComponentFactoriesEvent registry) {
        registry.register(ResonantPrismItem.SoundfontTooltip.class, ResonantPrismTooltip::new);
    }
}
