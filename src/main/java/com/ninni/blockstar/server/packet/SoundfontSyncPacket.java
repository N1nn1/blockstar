package com.ninni.blockstar.server.packet;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.server.data.SoundfontManager.SoundfontDefinition;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.function.Supplier;

public class SoundfontSyncPacket {
    private static final Gson GSON = new GsonBuilder().create();

    public BiMap<ResourceLocation, SoundfontDefinition> registryMap;

    public SoundfontSyncPacket(BiMap<ResourceLocation, SoundfontDefinition> registryMap) {
        this.registryMap = registryMap;
    }

    public SoundfontSyncPacket() {}

    public static SoundfontSyncPacket decode(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        BiMap<ResourceLocation, SoundfontDefinition> map = HashBiMap.create(size);

        for (int i = 0; i < size; i++) {
            ResourceLocation id = buf.readResourceLocation();

            String json = buf.readUtf();
            JsonElement elem = GSON.fromJson(json, JsonElement.class);
            SoundfontDefinition def = SoundfontDefinition.CODEC.parse(JsonOps.INSTANCE, elem).result().orElse(null);
            if (def != null) map.put(id, def);
            else Blockstar.LOGGER.error("Failed to decode SoundfontDefinition for {}", id);
        }

        SoundfontSyncPacket pkt = new SoundfontSyncPacket();
        pkt.registryMap = map;
        return pkt;
    }

    public static void encode(SoundfontSyncPacket message, FriendlyByteBuf buf) {
        buf.writeVarInt(message.registryMap.size());
        for (Map.Entry<ResourceLocation, SoundfontDefinition> e : message.registryMap.entrySet()) {
            buf.writeResourceLocation(e.getKey());

            JsonElement elem = SoundfontDefinition.CODEC.encodeStart(JsonOps.INSTANCE, e.getValue()).result().orElseThrow(() -> new IllegalStateException("Failed to encode SoundfontDefinition " + e.getKey()));

            buf.writeUtf(GSON.toJson(elem));
        }
    }

    public static void handle(SoundfontSyncPacket message, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
                Blockstar.PROXY.getSoundfontManager().synchronizeRegistryForClient(message.registryMap);
            }
        });
        context.setPacketHandled(true);
    }
}