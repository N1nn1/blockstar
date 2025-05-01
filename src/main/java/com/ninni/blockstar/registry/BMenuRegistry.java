package com.ninni.blockstar.registry;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.server.inventory.ComposingTableMenu;
import com.ninni.blockstar.server.inventory.KeyboardMenu;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BMenuRegistry {
    public static final DeferredRegister<MenuType<?>> DEF_REG = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Blockstar.MODID);

    public static final RegistryObject<MenuType<ComposingTableMenu>> COMPOSING_TABLE = DEF_REG.register("composing_table", () -> new MenuType<>(ComposingTableMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static final RegistryObject<MenuType<KeyboardMenu>> KEYBOARD = DEF_REG.register("keyboard", () -> new MenuType<>(KeyboardMenu::new, FeatureFlags.DEFAULT_FLAGS));

}
