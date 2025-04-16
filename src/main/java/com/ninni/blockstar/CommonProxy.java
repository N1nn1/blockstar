package com.ninni.blockstar;

import com.ninni.blockstar.server.event.CommonEvents;
import com.ninni.blockstar.server.packet.BNetworking;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.server.ServerLifecycleHooks;

public class CommonProxy {

    public void init() {
        MinecraftForge.EVENT_BUS.register(new CommonEvents());
    }

    public Level getWorld() {
        return ServerLifecycleHooks.getCurrentServer().overworld();
    }

    public void commonSetup() {
        BNetworking.register();
    }

    public void clientSetup() {
    }
}
