package com.ninni.blockstar.client.event;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.registry.BCreativeModeTabRegistry;
import com.ninni.blockstar.registry.BItemRegistry;
import com.ninni.blockstar.server.data.SoundfontManager;
import com.ninni.blockstar.server.item.ResonantPrismItem;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.MutableHashedLinkedMap;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
            List<SoundfontManager.SoundfontDefinition> uncategorized = new ArrayList<>();
            List<SoundfontManager.SoundfontDefinition> categorized = new ArrayList<>();

            for (SoundfontManager.SoundfontDefinition data : Blockstar.PROXY.getSoundfontManager().getAll()) {
                if (data.creativeTab()) {
                    if (!data.name().getPath().contains("-")) {
                        if (uncategorized.stream().noneMatch(soundfontDefinition -> soundfontDefinition.name() == data.name())) uncategorized.add(data);
                    }
                    else {
                        if (categorized.stream().noneMatch(soundfontDefinition -> soundfontDefinition.name() == data.name())) categorized.add(data);
                    }
                }
            }

            categorized.sort(Comparator.comparing(data -> data.name().getPath().split("-")[0]));

            Collections.reverse(uncategorized);
            Collections.reverse(categorized);

            for (SoundfontManager.SoundfontDefinition data : categorized) {
                entries.putAfter(BItemRegistry.RESONANT_PRISM.get().getDefaultInstance(), ResonantPrismItem.getPrismItemFromSoundfont(data), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            }
            for (SoundfontManager.SoundfontDefinition data : uncategorized) {
                entries.putAfter(BItemRegistry.RESONANT_PRISM.get().getDefaultInstance(), ResonantPrismItem.getPrismItemFromSoundfont(data), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            }
        }
    }

}
