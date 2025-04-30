package com.ninni.blockstar.client;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.CommonProxy;
import com.ninni.blockstar.client.event.ClientEvents;
import com.ninni.blockstar.client.midi.MidiSettingsConfig;
import com.ninni.blockstar.client.gui.KeyboardScreen;
import com.ninni.blockstar.client.misc.text.ResonantPrismTooltip;
import com.ninni.blockstar.client.sound.SoundManagerHelper;
import com.ninni.blockstar.client.sound.SoundfontSound;
import com.ninni.blockstar.registry.BItemRegistry;
import com.ninni.blockstar.registry.BMenuRegistry;
import com.ninni.blockstar.server.item.ResonantPrismItem;
import com.ninni.blockstar.server.midi.MidiInputHandler;
import com.ninni.blockstar.server.packet.PlaySoundPacket;
import com.ninni.blockstar.server.packet.StopSoundPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientProxy extends CommonProxy {

    @Override
    public void init() {
        MidiSettingsConfig.load();
        super.init();
        MidiInputHandler.startListening();

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::registerTooltips);
        bus.register(new ClientEvents());
    }

    @Override
    public void clientSetup() {
        MenuScreens.register(BMenuRegistry.KEYBOARD.get(), KeyboardScreen::new);

        ItemProperties.register(BItemRegistry.RESONANT_PRISM.get(), new ResourceLocation(Blockstar.MODID, "attuned"), (stack, level, player, i) -> {
            return stack.getOrCreateTag().contains("Soundfont") ? 1.0F : 0.0F;
        });
    }

    @Override
    public void handlePlaySoundPacket(PlaySoundPacket msg) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player != null) {
            Entity playerEntity = player.level().getEntity(msg.playerId);
            if (playerEntity instanceof LocalPlayer targetPlayer) {
                mc.getSoundManager().play(new SoundfontSound(msg.note, msg.soundLocation, 1.0f, msg.pitch, targetPlayer));
            }
        }
    }

    @Override
    public boolean isScreenShiftDown() {
        return Screen.hasShiftDown();
    }

    @Override
    public void handleStopSoundPacket(StopSoundPacket msg) {
        SoundManagerHelper.releaseMatchingSound(msg.note, msg.userId, msg.releaseTicks);
    }

    private void registerTooltips(RegisterClientTooltipComponentFactoriesEvent registry) {
        registry.register(ResonantPrismItem.SoundfontTooltip.class, ResonantPrismTooltip::new);
    }
}
