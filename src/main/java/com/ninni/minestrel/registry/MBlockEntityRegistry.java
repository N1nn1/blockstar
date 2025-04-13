package com.ninni.minestrel.registry;

import com.ninni.minestrel.Minestrel;
import com.ninni.minestrel.server.block.entity.KeyboardBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MBlockEntityRegistry {
    public static final DeferredRegister<BlockEntityType<?>> DEF_REG = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Minestrel.MODID);

    public static final RegistryObject<BlockEntityType<KeyboardBlockEntity>> KEYBOARD = DEF_REG.register("keyboard", () -> BlockEntityType.Builder.of(KeyboardBlockEntity::new, MBlockRegistry.KEYBOARD.get()).build(null));
}
