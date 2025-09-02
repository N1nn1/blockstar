package com.ninni.blockstar.client;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.CommonProxy;
import com.ninni.blockstar.client.config.KeyboardSettingsConfig;
import com.ninni.blockstar.client.event.ForgeClientEvents;
import com.ninni.blockstar.client.gui.ComposingTableScreen;
import com.ninni.blockstar.client.config.MidiSettingsConfig;
import com.ninni.blockstar.client.gui.KeyboardScreen;
import com.ninni.blockstar.registry.BItemRegistry;
import com.ninni.blockstar.registry.BMenuRegistry;
import com.ninni.blockstar.server.block.RodType;
import com.ninni.blockstar.server.item.MetronomeItem;
import com.ninni.blockstar.server.packet.PlaySoundPacket;
import com.ninni.blockstar.server.packet.StopSoundPacket;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class ClientProxy extends CommonProxy {

    @Override
    public void init() {
        MidiSettingsConfig.load();
        KeyboardSettingsConfig.load();

        super.init();
    }

    @Override
    public void clientSetup() {
        MenuScreens.register(BMenuRegistry.COMPOSING_TABLE.get(), ComposingTableScreen::new);
        MenuScreens.register(BMenuRegistry.KEYBOARD.get(), KeyboardScreen::new);

        ItemProperties.register(BItemRegistry.RESONANT_PRISM.get(), new ResourceLocation(Blockstar.MODID, "attuned"), (stack, level, player, i) -> {
            return stack.getOrCreateTag().contains("Soundfont") ? 1.0F : 0.0F;
        });

        ItemProperties.register(BItemRegistry.METRONOME.get(), new ResourceLocation("swing"), (stack, level, entity, seed) -> {
            if (!(stack.getItem() instanceof MetronomeItem)) return 0.0F;
            if (!MetronomeItem.isTicking(stack)) return 0.0F;

            UUID id = MetronomeItem.getOrCreateUniqueID(stack);
            RodType rod = ForgeClientEvents.getItemRod(id);

            return switch (rod) {
                case LEFT -> 1.0F;
                case RIGHT -> 2.0F;
                case MIDDLE -> 0.0F;
            };
        });
    }

    @Override
    public void handlePlaySoundPacket(PlaySoundPacket msg) {
        MyNewThing.handlePlaySoundPacket(msg);
    }

    @Override
    public boolean isScreenShiftDown() {
        return MyNewThing.isScreenShiftDown();
    }

    @Override
    public Level getWorld() {
        return MyNewThing.getWorld();
    }

    @Override
    public void handleStopSoundPacket(StopSoundPacket msg) {
        MyNewThing.handleStopSoundPacket(msg);
    }

    @Override
    public void openMetronomeScreen(LocalPlayer localPlayer, ItemStack itemStack) {
        MyNewThing.openMetronomeScreen(localPlayer, itemStack);
    }
}
