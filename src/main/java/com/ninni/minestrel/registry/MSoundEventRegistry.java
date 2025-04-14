package com.ninni.minestrel.registry;

import com.ninni.minestrel.Minestrel;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class MSoundEventRegistry {
    public static final DeferredRegister<SoundEvent> DEF_REG = DeferredRegister.create(Registries.SOUND_EVENT, Minestrel.MODID);

    public static RegistryObject<SoundEvent> register(ResourceLocation location) {
        return DEF_REG.register(location.getPath(), () -> SoundEvent.createVariableRangeEvent(location));
    }
}
