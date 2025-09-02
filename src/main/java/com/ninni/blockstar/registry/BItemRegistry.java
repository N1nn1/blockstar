package com.ninni.blockstar.registry;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.server.item.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.RecordItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BItemRegistry {
    public static final DeferredRegister<Item> DEF_REG = DeferredRegister.create(ForgeRegistries.ITEMS, Blockstar.MODID);

    //Resonant Amethyst
    public static final RegistryObject<Item> RESONANT_AMETHYST_BLOCK = DEF_REG.register("resonant_amethyst_block", () -> new BlockItem(BBlockRegistry.RESONANT_AMETHYST_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> RESONANT_AMETHYST_SHARD = DEF_REG.register("resonant_amethyst_shard", () -> new Item(new Item.Properties()));

    //Crafting Ingredients
    public static final RegistryObject<Item> RESONANT_PRISM = DEF_REG.register("resonant_prism", () -> new ResonantPrismItem(new Item.Properties()));
    public static final RegistryObject<Item> MUSIC_DISC_TEMPLATE = DEF_REG.register("music_disc_template", () -> new ObfuscatedRecordItem(1, BSoundEventRegistry.MUSIC_DISC_DITTYBIT, new Item.Properties().stacksTo(1), 107 * 20));

    //Craftables/Utility
    public static final RegistryObject<Item> METRONOME = DEF_REG.register("metronome", MetronomeItem::new);

    //Workstations
    public static final RegistryObject<Item> COMPOSING_TABLE = DEF_REG.register("composing_table", ComposingTableItem::new);
    public static final RegistryObject<Item> SHEET_MUSIC = DEF_REG.register("sheet_music", () -> new SheetMusicItem(new Item.Properties().stacksTo(1)));

    //Instruments
    public static final RegistryObject<Item> KEYBOARD = DEF_REG.register("keyboard", () -> new BlockItem(BBlockRegistry.KEYBOARD.get(), new Item.Properties()));

}
