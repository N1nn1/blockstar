package com.ninni.blockstar.registry;

import com.ninni.blockstar.server.packet.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

import static com.ninni.blockstar.Blockstar.MODID;

public class BNetwork {
    private static final ResourceLocation PACKET_NETWORK_NAME = new ResourceLocation(MODID, "main");
    private static final String PROTOCOL_VERSION = Integer.toString(1);
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            PACKET_NETWORK_NAME,
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    private static int id = 0;

    public static void register() {
        INSTANCE.registerMessage(id++, PlaySoundPacket.class, PlaySoundPacket::encode, PlaySoundPacket::decode, PlaySoundPacket::handle);
        INSTANCE.registerMessage(id++, StopSoundPacket.class, StopSoundPacket::encode, StopSoundPacket::decode, StopSoundPacket::handle);
        INSTANCE.registerMessage(id++, SheetNoteEditPacket.class, SheetNoteEditPacket::encode, SheetNoteEditPacket::decode, SheetNoteEditPacket::handle);
        INSTANCE.registerMessage(id++, SheetSettingsUpdatePacket.class, SheetSettingsUpdatePacket::encode, SheetSettingsUpdatePacket::decode, SheetSettingsUpdatePacket::handle);
        INSTANCE.registerMessage(id++, SheetRenamePacket.class, SheetRenamePacket::encode, SheetRenamePacket::decode, SheetRenamePacket::handle);
        INSTANCE.registerMessage(id++, BlockEntitySyncPacket.class, BlockEntitySyncPacket::encode, BlockEntitySyncPacket::decode, BlockEntitySyncPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        INSTANCE.registerMessage(id++, MetronomeUpdatePacket.class, MetronomeUpdatePacket::encode, MetronomeUpdatePacket::decode, MetronomeUpdatePacket::handle);
        INSTANCE.registerMessage(id++, MetronomeTogglePacket.class, MetronomeTogglePacket::encode, MetronomeTogglePacket::decode, MetronomeTogglePacket::handle);
        INSTANCE.registerMessage(id++, SoundfontSyncPacket.class, SoundfontSyncPacket::encode, SoundfontSyncPacket::decode, SoundfontSyncPacket::handle);
    }
}