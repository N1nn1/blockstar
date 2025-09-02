package com.ninni.blockstar.client;

import com.ninni.blockstar.client.gui.MetronomeScreen;
import com.ninni.blockstar.client.sound.SoundManagerHelper;
import com.ninni.blockstar.client.sound.SoundfontSound;
import com.ninni.blockstar.server.packet.S2CPlaySoundPacket;
import com.ninni.blockstar.server.packet.S2CStopSoundPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ClientHandler {

    public static void handlePlaySoundPacket(S2CPlaySoundPacket msg) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null) return;

        Entity entity = level.getEntity(msg.playerId);
        LivingEntity attach = (entity instanceof LivingEntity living) ? living : null;

        mc.getSoundManager().play(new SoundfontSound(msg.note, msg.soundId, 1.0f, msg.pitch, attach, msg.autoFadeTicks));
    }

    public static boolean isScreenShiftDown() {
        return Screen.hasShiftDown();
    }

    public static Level getWorld() {
        return Minecraft.getInstance().level;
    }

    public static void handleStopSoundPacket(S2CStopSoundPacket msg) {
        SoundManagerHelper.releaseMatchingSound(msg.note, msg.userId, msg.releaseTicks);
    }

    public static void openMetronomeScreen(LocalPlayer localPlayer, ItemStack itemStack) {
        localPlayer.playNotifySound(SoundEvents.UI_BUTTON_CLICK.get(), SoundSource.MASTER, 0.15F, 1);
        Minecraft.getInstance().setScreen(new MetronomeScreen(itemStack));
    }
}
