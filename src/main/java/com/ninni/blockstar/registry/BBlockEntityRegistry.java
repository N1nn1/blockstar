package com.ninni.blockstar.registry;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.server.block.entity.ComposingTableBlockEntity;
import com.ninni.blockstar.server.block.entity.KeyboardBlockEntity;
import com.ninni.blockstar.server.block.entity.MetronomeBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BBlockEntityRegistry {
    public static final DeferredRegister<BlockEntityType<?>> DEF_REG = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Blockstar.MODID);

    public static final RegistryObject<BlockEntityType<ComposingTableBlockEntity>> COMPOSING_TABLE = DEF_REG.register("composing_table", () -> BlockEntityType.Builder.of(ComposingTableBlockEntity::new, BBlockRegistry.COMPOSING_TABLE.get()).build(null));
    public static final RegistryObject<BlockEntityType<MetronomeBlockEntity>> METRONOME = DEF_REG.register("metronome", () -> BlockEntityType.Builder.of(MetronomeBlockEntity::new, BBlockRegistry.METRONOME.get()).build(null));

    public static final RegistryObject<BlockEntityType<KeyboardBlockEntity>> KEYBOARD = DEF_REG.register("keyboard", () -> BlockEntityType.Builder.of(KeyboardBlockEntity::new, BBlockRegistry.KEYBOARD.get()).build(null));
}
