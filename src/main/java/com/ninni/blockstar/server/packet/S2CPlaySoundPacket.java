package com.ninni.blockstar.server.packet;

import com.ninni.blockstar.Blockstar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

public class S2CPlaySoundPacket {
    public final ResourceLocation soundId;
    public final float pitch;
    public final int playerId;
    public final int note;
    public final Optional<Integer> autoFadeTicks;

    public S2CPlaySoundPacket(ResourceLocation soundId, float pitch, int playerId, int note, Optional<Integer> autoFadeTicks) {
        this.soundId = soundId;
        this.pitch = pitch;
        this.playerId = playerId;
        this.note = note;
        this.autoFadeTicks = autoFadeTicks;
    }

    public static void encode(S2CPlaySoundPacket msg, FriendlyByteBuf buf) {
        buf.writeResourceLocation(msg.soundId);
        buf.writeFloat(msg.pitch);
        buf.writeVarInt(msg.playerId);
        buf.writeVarInt(msg.note);
        buf.writeBoolean(msg.autoFadeTicks.isPresent());
        msg.autoFadeTicks.ifPresent(buf::writeVarInt);
    }

    public static S2CPlaySoundPacket decode(FriendlyByteBuf buf) {
        ResourceLocation id = buf.readResourceLocation();
        float pitch = buf.readFloat();
        int srcId = buf.readVarInt();
        int note = buf.readVarInt();
        Optional<Integer> vel = buf.readBoolean() ? Optional.of(buf.readVarInt()) : Optional.empty();
        return new S2CPlaySoundPacket(id, pitch, srcId, note, vel);
    }

    public static void handle(S2CPlaySoundPacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            ClientLevel level = mc.level;
            if (level == null) return;

            Blockstar.PROXY.handlePlaySoundPacket(msg);
        });
        context.setPacketHandled(true);
    }
}