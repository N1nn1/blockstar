package com.ninni.blockstar.registry;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.server.item.ComposingTableItem;
import com.ninni.blockstar.server.item.ResonantPrismItem;
import com.ninni.blockstar.server.item.SheetMusicItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BItemRegistry {
    public static final DeferredRegister<Item> DEF_REG = DeferredRegister.create(ForgeRegistries.ITEMS, Blockstar.MODID);

    public static final RegistryObject<Item> COMPOSING_TABLE = DEF_REG.register("composing_table", ComposingTableItem::new);
    public static final RegistryObject<Item> SHEET_MUSIC = DEF_REG.register("sheet_music", () -> new SheetMusicItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> KEYBOARD = DEF_REG.register("keyboard", () -> new BlockItem(BBlockRegistry.KEYBOARD.get(), new Item.Properties()));

    public static final RegistryObject<Item> RESONANT_PRISM = DEF_REG.register("resonant_prism", () -> new ResonantPrismItem(new Item.Properties()));
}
