package com.ninni.blockstar.registry;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.server.block.ComposingTableBlock;
import com.ninni.blockstar.server.block.KeyboardBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BBlockRegistry {
    public static final DeferredRegister<Block> DEF_REG = DeferredRegister.create(ForgeRegistries.BLOCKS, Blockstar.MODID);

    public static final RegistryObject<Block> COMPOSING_TABLE = DEF_REG.register("composing_table", ComposingTableBlock::new);

    public static final RegistryObject<Block> KEYBOARD = DEF_REG.register("keyboard", () -> new KeyboardBlock(BInstrumentTypeRegistry.KEYBOARD));
}
