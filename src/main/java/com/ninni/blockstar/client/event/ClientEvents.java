package com.ninni.blockstar.client.event;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.client.misc.text.ComposingTableTooltip;
import com.ninni.blockstar.client.misc.text.ResonantPrismTooltip;
import com.ninni.blockstar.registry.BCreativeModeTabRegistry;
import com.ninni.blockstar.registry.BItemRegistry;
import com.ninni.blockstar.server.data.SoundfontManager;
import com.ninni.blockstar.server.item.ComposingTableItem;
import com.ninni.blockstar.server.item.ResonantPrismItem;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.common.util.MutableHashedLinkedMap;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Mod.EventBusSubscriber(modid = Blockstar.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public void onClientSetup(final FMLClientSetupEvent event) {
    }

    @SubscribeEvent
    public static void registerCreativeModeTab(BuildCreativeModeTabContentsEvent event) {
        ResourceKey<CreativeModeTab> key = event.getTabKey();
        MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility> entries = event.getEntries();

        if (key == BCreativeModeTabRegistry.BLOCKSTAR.getKey()) {
            List<SoundfontManager.SoundfontDefinition> uncategorized = new ArrayList<>();
            List<SoundfontManager.SoundfontDefinition> categorized = new ArrayList<>();

            for (SoundfontManager.SoundfontDefinition data : Blockstar.PROXY.getSoundfontManager().getAll()) {
                if (data.creativeTab()) {
                    if (!data.name().getPath().contains("-")) {
                        if (uncategorized.stream().noneMatch(soundfontDefinition -> soundfontDefinition.name().equals(data.name()))) uncategorized.add(data);
                    }
                    else {
                        if (categorized.stream().noneMatch(soundfontDefinition -> soundfontDefinition.name().equals(data.name()))) categorized.add(data);
                    }
                }
            }

            categorized.sort(Comparator.comparing(data -> data.name().getPath().split("-")[0]));

            for (SoundfontManager.SoundfontDefinition data : uncategorized) {
                entries.put(ResonantPrismItem.getPrismItemFromSoundfont(data), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            }
            for (SoundfontManager.SoundfontDefinition data : categorized) {
                entries.put(ResonantPrismItem.getPrismItemFromSoundfont(data), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            }
        }


        if (key == CreativeModeTabs.BUILDING_BLOCKS) {
            entries.putAfter(Items.AMETHYST_BLOCK.getDefaultInstance(), BItemRegistry.RESONANT_AMETHYST_BLOCK.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        if (key == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            entries.putAfter(Items.JUKEBOX.getDefaultInstance(), BItemRegistry.METRONOME.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(BItemRegistry.METRONOME.get().getDefaultInstance(), BItemRegistry.KEYBOARD.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(Items.LOOM.getDefaultInstance(), BItemRegistry.COMPOSING_TABLE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        if (key == CreativeModeTabs.REDSTONE_BLOCKS) {
            entries.putAfter(Items.JUKEBOX.getDefaultInstance(), BItemRegistry.METRONOME.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        if (key == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            entries.putBefore(Items.MUSIC_DISC_13.getDefaultInstance(), BItemRegistry.MUSIC_DISC_TEMPLATE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        if (key == CreativeModeTabs.INGREDIENTS) {
            entries.putAfter(Items.AMETHYST_SHARD.getDefaultInstance(), BItemRegistry.RESONANT_AMETHYST_SHARD.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(BItemRegistry.RESONANT_AMETHYST_SHARD.get().getDefaultInstance(), BItemRegistry.RESONANT_PRISM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }

    @SubscribeEvent
    public static void registerTooltips(RegisterClientTooltipComponentFactoriesEvent registry) {
        registry.register(ResonantPrismItem.SoundfontTooltip.class, ResonantPrismTooltip::new);
        registry.register(ComposingTableItem.ComposingTableTooltip.class, ComposingTableTooltip::new);
    }
}
