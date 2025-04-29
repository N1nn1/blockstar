package com.ninni.blockstar.client;

import com.ninni.blockstar.CommonProxy;
import com.ninni.blockstar.client.event.ClientEvents;
import com.ninni.blockstar.client.midi.MidiSettingsConfig;
import com.ninni.blockstar.client.gui.KeyboardScreen;
import com.ninni.blockstar.client.misc.text.ResonantPrismTooltip;
import com.ninni.blockstar.client.sound.SoundfontSound;
import com.ninni.blockstar.registry.BMenuRegistry;
import com.ninni.blockstar.server.item.ResonantPrismItem;
import com.ninni.blockstar.server.packet.PlaySoundPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
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
        bus.register(new ClientEvents());
    }

    @Override
    public void clientSetup() {
        MenuScreens.register(BMenuRegistry.KEYBOARD.get(), KeyboardScreen::new);
    }

    @Override
    public void handlePlaySoundPacket(PlaySoundPacket msg) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player != null) {
            Entity playerEntity = player.level().getEntity(msg.playerId);
            if (playerEntity instanceof Player targetPlayer) {
                mc.getSoundManager().play(new SoundfontSound(msg.soundLocation, 1.0f, msg.pitch, targetPlayer));
            }
        }
    }

    private void registerTooltips(RegisterClientTooltipComponentFactoriesEvent registry) {
        registry.register(ResonantPrismItem.SoundfontTooltip.class, ResonantPrismTooltip::new);
    }
}
