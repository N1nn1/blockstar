package com.ninni.minestrel.registry;

import com.ninni.minestrel.Minestrel;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Minestrel.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MCreativeModeTabRegistry {

    public static final DeferredRegister<CreativeModeTab> DEF_REG = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Minestrel.MODID);

    public static final RegistryObject<CreativeModeTab> MINESTREL = DEF_REG.register("minestrel", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.minestrel.minestrel")).icon(MItemRegistry.KEYBOARD.get()::getDefaultInstance)
            .displayItems((itemDisplayParameters, output) -> {
                MItemRegistry.DEF_REG.getEntries().forEach(itemRegistryObject ->  {
                    output.accept(itemRegistryObject.get());
                });
            })
            .build());

}
