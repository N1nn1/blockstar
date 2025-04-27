package com.ninni.blockstar.client.event;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.client.gui.MidiSettingsScreen;
import com.ninni.blockstar.registry.BCreativeModeTabRegistry;
import com.ninni.blockstar.registry.BInstrumentTypeRegistry;
import com.ninni.blockstar.registry.BItemRegistry;
import com.ninni.blockstar.server.data.SoundfontManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.SoundOptionsScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.util.MutableHashedLinkedMap;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Blockstar.MODID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public void onClientSetup(final FMLClientSetupEvent event) {
        Blockstar.CALLBACKS.forEach(Runnable::run);
        Blockstar.CALLBACKS.clear();
    }

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init event) {
        if (event.getScreen() instanceof SoundOptionsScreen soundOptionsScreen) {

            int x = soundOptionsScreen.width / 2 + 5;
            int y = soundOptionsScreen.height / 6 + 105;

            Button button1 = Button.builder(Component.translatable("blockstar.options.midi.title"), (button) -> Minecraft.getInstance().setScreen(new MidiSettingsScreen(soundOptionsScreen, soundOptionsScreen.options))).bounds(x, y, 150, 20).build();
            soundOptionsScreen.children.add(button1);
            soundOptionsScreen.narratables.add(button1);
            soundOptionsScreen.renderables.add(button1);
        }
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
