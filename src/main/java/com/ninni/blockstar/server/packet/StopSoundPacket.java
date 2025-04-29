package com.ninni.blockstar.server.packet;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.client.sound.SoundManagerHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class StopSoundPacket {
    private final ResourceLocation soundLocation;
    private final float pitch;
    private final double x, y, z;
    private final int fadeTicks;

    public StopSoundPacket(ResourceLocation soundLocation, float pitch, double x, double y, double z, int fadeTicks) {
        this.soundLocation = soundLocation;
        this.pitch = pitch;
        this.x = x;
        this.y = y;
        this.z = z;
        this.fadeTicks = fadeTicks;
    }

    public static void encode(StopSoundPacket msg, FriendlyByteBuf buf) {
        buf.writeResourceLocation(msg.soundLocation);
        buf.writeFloat(msg.pitch);
        buf.writeDouble(msg.x);
        buf.writeDouble(msg.y);
        buf.writeDouble(msg.z);
        buf.writeInt(msg.fadeTicks);
    }

    public static StopSoundPacket decode(FriendlyByteBuf buf) {
        return new StopSoundPacket(buf.readResourceLocation(), buf.readFloat(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readInt());
    }

    public static void handle(StopSoundPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> SoundManagerHelper.fadeOutMatchingSound(msg.soundLocation, msg.pitch, msg.x, msg.y, msg.z, msg.fadeTicks));
        ctx.get().setPacketHandled(true);
    }
}

