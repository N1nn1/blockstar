package com.ninni.minestrel;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import com.ninni.minestrel.registry.MSoundEventRegistry;
import com.ninni.minestrel.server.event.CommonEventHandler;
import com.ninni.minestrel.server.soundfont.SoundfontManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.MinecraftForge;

import java.util.Map;

public class CommonProxy {

    public void init() {

        MinecraftForge.EVENT_BUS.register(new CommonEventHandler());
    }

    public void commonSetup() {
    }

    public void clientSetup() {
    }
}
