package com.ninni.minestrel.registry;

import com.ninni.minestrel.Minestrel;
import com.ninni.minestrel.server.block.KeyboardBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MBlockRegistry {
    public static final DeferredRegister<Block> DEF_REG = DeferredRegister.create(ForgeRegistries.BLOCKS, Minestrel.MODID);

    public static final RegistryObject<Block> KEYBOARD = DEF_REG.register("keyboard", KeyboardBlock::new);
}
