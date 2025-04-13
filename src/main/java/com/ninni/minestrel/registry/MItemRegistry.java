package com.ninni.minestrel.registry;

import com.ninni.minestrel.Minestrel;
import com.ninni.minestrel.server.block.KeyboardBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MItemRegistry {
    public static final DeferredRegister<Item> DEF_REG = DeferredRegister.create(ForgeRegistries.ITEMS, Minestrel.MODID);

    public static final RegistryObject<Item> KEYBOARD = DEF_REG.register("keyboard", () -> new BlockItem(MBlockRegistry.KEYBOARD.get(), new Item.Properties()));
}
