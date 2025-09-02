package com.ninni.blockstar.client;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.CommonProxy;
import com.ninni.blockstar.client.config.KeyboardSettingsConfig;
import com.ninni.blockstar.client.event.ClientEvents;
import com.ninni.blockstar.client.event.ForgeClientEvents;
import com.ninni.blockstar.client.gui.ComposingTableScreen;
import com.ninni.blockstar.client.config.MidiSettingsConfig;
import com.ninni.blockstar.client.gui.KeyboardScreen;
import com.ninni.blockstar.client.gui.MetronomeScreen;
import com.ninni.blockstar.client.sound.SoundManagerHelper;
import com.ninni.blockstar.client.sound.SoundfontSound;
import com.ninni.blockstar.registry.BItemRegistry;
import com.ninni.blockstar.registry.BMenuRegistry;
import com.ninni.blockstar.server.block.RodType;
import com.ninni.blockstar.server.item.MetronomeItem;
import com.ninni.blockstar.server.midi.MidiInputHandler;
import com.ninni.blockstar.server.packet.PlaySoundPacket;
import com.ninni.blockstar.server.packet.StopSoundPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;

import java.util.UUID;

public class ClientProxy extends CommonProxy {

    @Override
    public void init() {
        MidiSettingsConfig.load();
        KeyboardSettingsConfig.load();
        super.init();
        MidiInputHandler.startListening();
        MinecraftForge.EVENT_BUS.register(new ClientEvents());
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
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player != null) {
            Entity playerEntity = player.level().getEntity(msg.playerId);
            if (playerEntity instanceof LocalPlayer targetPlayer) {
                mc.getSoundManager().play(new SoundfontSound(msg.note, msg.soundLocation, 1.0f, msg.pitch, targetPlayer, msg.autoFadeTicks));
            }
        }
    }

    @Override
    public boolean isScreenShiftDown() {
        return Screen.hasShiftDown();
    }

    @Override
    public Level getWorld() {
        return Minecraft.getInstance().level;
    }

    @Override
    public void handleStopSoundPacket(StopSoundPacket msg) {
        SoundManagerHelper.releaseMatchingSound(msg.note, msg.userId, msg.releaseTicks);
    }

    @Override
    public void openMetronomeScreen(LocalPlayer localPlayer, ItemStack itemStack) {
        localPlayer.playNotifySound(SoundEvents.UI_BUTTON_CLICK.get(), SoundSource.MASTER, 0.15F, 1);
        Minecraft.getInstance().setScreen(new MetronomeScreen(itemStack));
    }
}
