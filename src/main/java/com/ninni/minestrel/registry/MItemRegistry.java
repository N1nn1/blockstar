package com.ninni.minestrel.registry;

import com.ninni.minestrel.Minestrel;
import com.ninni.minestrel.server.block.KeyboardBlock;
import com.ninni.minestrel.server.item.SheetMusicItem;
import com.ninni.minestrel.server.item.StaffPaperItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MItemRegistry {
    public static final DeferredRegister<Item> DEF_REG = DeferredRegister.create(ForgeRegistries.ITEMS, Minestrel.MODID);

    public static final RegistryObject<Item> STAFF_PAPER = DEF_REG.register("staff_paper", () -> new StaffPaperItem(new Item.Properties()));
    public static final RegistryObject<Item> SHEET_MUSIC = DEF_REG.register("sheet_music", () -> new SheetMusicItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> KEYBOARD = DEF_REG.register("keyboard", () -> new BlockItem(MBlockRegistry.KEYBOARD.get(), new Item.Properties()));
}
