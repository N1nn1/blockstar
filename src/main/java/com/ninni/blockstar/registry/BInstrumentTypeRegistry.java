package com.ninni.blockstar.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.server.instrument.InstrumentType;
import com.ninni.blockstar.server.instrument.Keyboard;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;

public class BInstrumentTypeRegistry {
    public static final DeferredRegister<InstrumentType> DEF_REG = DeferredRegister.create(new ResourceLocation(Blockstar.MODID, "instrument_type"), Blockstar.MODID);
    public static final Codec<InstrumentType> CODEC = ResourceLocation.CODEC.flatXmap(
            id -> {
                InstrumentType type = BInstrumentTypeRegistry.getInternalRegistry().getValue(id);
                return type != null ? DataResult.success(type) : DataResult.error(() -> "Unknown instrument type: " + id);
            },
            instrumentType -> {
                Optional<ResourceKey<InstrumentType>> key = BInstrumentTypeRegistry.getInternalRegistry().getResourceKey(instrumentType);
                return key.map(resourceKey -> DataResult.success(resourceKey.location())).orElseGet(() -> DataResult.error(() -> "Unknown resource key for instrument type"));
            }
    );

    public static final RegistryObject<InstrumentType> KEYBOARD = DEF_REG.register("keyboard", Keyboard::new);
    private static IForgeRegistry<InstrumentType> internalRegistry;

    public static void setInternalRegistry(IForgeRegistry<InstrumentType> registry) {
        internalRegistry = registry;
    }
    public static IForgeRegistry<InstrumentType> getInternalRegistry() {
        return internalRegistry;
    }

    public static ResourceLocation get(InstrumentType instrumentType) {
        return internalRegistry.getKey(instrumentType);
    }

    public static InstrumentType readInstrument(FriendlyByteBuf buf) {
        return internalRegistry.getValue(buf.readResourceLocation());
    }

    public static FriendlyByteBuf writeInstrument(FriendlyByteBuf buf, InstrumentType instrumentType) {
        return buf.writeResourceLocation(internalRegistry.getResourceKey(instrumentType).orElse(KEYBOARD.getKey()).location());
    }
}
