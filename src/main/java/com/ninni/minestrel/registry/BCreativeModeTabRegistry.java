package com.ninni.minestrel.registry;

import com.ninni.minestrel.Blockstar;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Blockstar.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BCreativeModeTabRegistry {

    public static final DeferredRegister<CreativeModeTab> DEF_REG = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Blockstar.MODID);

    public static final RegistryObject<CreativeModeTab> BLOCKSTAR = DEF_REG.register("blockstar", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.blockstar.blockstar")).icon(BItemRegistry.KEYBOARD.get()::getDefaultInstance)
            .displayItems((itemDisplayParameters, output) -> {
                BItemRegistry.DEF_REG.getEntries().forEach(itemRegistryObject ->  {
                    output.accept(itemRegistryObject.get());
                });
            })
            .build());

}
