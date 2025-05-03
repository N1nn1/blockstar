package com.ninni.blockstar.registry;

import com.ninni.blockstar.Blockstar;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BSoundEventRegistry {
    public static final DeferredRegister<SoundEvent> DEF_REG = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Blockstar.MODID);

    public static final RegistryObject<SoundEvent> RESONANT_PRISM_TUNE = createSoundEvent("item.resonant_prism.tune");
    public static final RegistryObject<SoundEvent> METRONOME_DOWNBEAT = createSoundEvent("block.metronome.downbeat");
    public static final RegistryObject<SoundEvent> METRONOME_BEAT = createSoundEvent("block.metronome.beat");

    private static RegistryObject<SoundEvent> createSoundEvent(final String soundName) {
        return DEF_REG.register(soundName, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Blockstar.MODID, soundName)));
    }
}
