package com.ninni.blockstar;

import com.ninni.blockstar.server.data.SoundfontManager;
import com.ninni.blockstar.server.packet.PlaySoundPacket;
import com.ninni.blockstar.server.packet.StopSoundPacket;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;

public class CommonProxy {
    private final SoundfontManager soundfontManager = new SoundfontManager();

    public void init() {
    }

    public void commonSetup() {
    }

    public void clientSetup() {
    }

    public void handlePlaySoundPacket(PlaySoundPacket msg) {
    }

    public void handleStopSoundPacket(StopSoundPacket msg) {
    }

    public void openMetronomeScreen(LocalPlayer localPlayer, ItemStack itemStack) {
    }

    public Level getWorld() {
        return ServerLifecycleHooks.getCurrentServer().overworld();
    }

    public boolean isScreenShiftDown() {
        return false;
    }

    public SoundfontManager getSoundfontManager() {
        return soundfontManager;
    }
}
