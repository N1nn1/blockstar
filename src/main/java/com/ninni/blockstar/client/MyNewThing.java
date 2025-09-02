package com.ninni.blockstar.client;

import com.ninni.blockstar.client.gui.MetronomeScreen;
import com.ninni.blockstar.client.sound.SoundManagerHelper;
import com.ninni.blockstar.client.sound.SoundfontSound;
import com.ninni.blockstar.server.packet.PlaySoundPacket;
import com.ninni.blockstar.server.packet.StopSoundPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MyNewThing {

    public static void handlePlaySoundPacket(PlaySoundPacket msg) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player != null) {
            Entity playerEntity = player.level().getEntity(msg.playerId);
            if (playerEntity instanceof LocalPlayer targetPlayer) {
                mc.getSoundManager().play(new SoundfontSound(msg.note, msg.soundLocation, 1.0f, msg.pitch, targetPlayer, msg.autoFadeTicks));
            }
        }
    }

    public static boolean isScreenShiftDown() {
        return Screen.hasShiftDown();
    }

    public static Level getWorld() {
        return Minecraft.getInstance().level;
    }

    public static void handleStopSoundPacket(StopSoundPacket msg) {
        SoundManagerHelper.releaseMatchingSound(msg.note, msg.userId, msg.releaseTicks);
    }

    public static void openMetronomeScreen(LocalPlayer localPlayer, ItemStack itemStack) {
        localPlayer.playNotifySound(SoundEvents.UI_BUTTON_CLICK.get(), SoundSource.MASTER, 0.15F, 1);
        Minecraft.getInstance().setScreen(new MetronomeScreen(itemStack));
    }
}
