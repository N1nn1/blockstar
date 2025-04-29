package com.ninni.blockstar.client.event;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.client.config.MidiSettingsConfig;
import com.ninni.blockstar.client.gui.MidiSettingsScreen;
import com.ninni.blockstar.registry.BCreativeModeTabRegistry;
import com.ninni.blockstar.registry.BInstrumentTypeRegistry;
import com.ninni.blockstar.registry.BItemRegistry;
import com.ninni.blockstar.server.data.SoundfontManager;
import com.ninni.blockstar.server.item.ResonantPrismItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.SoundOptionsScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.util.MutableHashedLinkedMap;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Mod.EventBusSubscriber(modid = Blockstar.MODID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public void onClientSetup(final FMLClientSetupEvent event) {
        Blockstar.CALLBACKS.forEach(Runnable::run);
        Blockstar.CALLBACKS.clear();
    }

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init event) {
        if (event.getScreen() instanceof SoundOptionsScreen screen) {

            int x = MidiSettingsConfig.parse(MidiSettingsConfig.buttonX.replace("width", String.valueOf(screen.width)));
            int y = MidiSettingsConfig.parse(MidiSettingsConfig.buttonY.replace("height", String.valueOf(screen.height)));


            Button button1 = Button.builder(Component.translatable("blockstar.options.midi.title"), (button) ->
                    Minecraft.getInstance().setScreen(new MidiSettingsScreen(screen, screen.options)))
                    .bounds(x, y, 80, 20)
                    .build();

            screen.children.add(button1);
            screen.narratables.add(button1);
            screen.renderables.add(button1);
        }
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
                    if (!data.name().getPath().contains("-")) uncategorized.add(data);
                    else categorized.add(data);
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
