package com.ninni.blockstar.client.event;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.registry.BCreativeModeTabRegistry;
import com.ninni.blockstar.registry.BInstrumentTypeRegistry;
import com.ninni.blockstar.registry.BItemRegistry;
import com.ninni.blockstar.server.data.SoundfontManager;
import com.ninni.blockstar.server.event.CommonEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.MutableHashedLinkedMap;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientEvents {

    @SubscribeEvent
    public void onClientSetup(final FMLClientSetupEvent event) {
        Blockstar.CALLBACKS.forEach(Runnable::run);
        Blockstar.CALLBACKS.clear();
    }


    @SubscribeEvent
    public void registerCreativeModeTab(BuildCreativeModeTabContentsEvent event) {
        ResourceKey<CreativeModeTab> key = event.getTabKey();
        MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility> entries = event.getEntries();

        if (key == BCreativeModeTabRegistry.BLOCKSTAR.getKey()) {
            for (SoundfontManager.SoundfontDefinition data : Blockstar.PROXY.getSoundfontManager().getAll()) {
                if (data.creativeTab()) {
                    CompoundTag stackTag = new CompoundTag();
                    stackTag.putString("Soundfont", data.name().toString());
                    if (data.instrumentExclusive()) stackTag.putString("InstrumentType", BInstrumentTypeRegistry.get(data.instrumentType()).toString());
                    if (data.rarity() != Rarity.COMMON) stackTag.putString("Rarity", data.rarity().toString());
                    ItemStack stack = BItemRegistry.RESONANT_PRISM.get().getDefaultInstance();
                    stack.setTag(stackTag);
                    entries.putAfter(BItemRegistry.RESONANT_PRISM.get().getDefaultInstance(), stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                }
            }
        }
    }
}
