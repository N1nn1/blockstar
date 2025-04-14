package com.ninni.minestrel;

import com.mojang.logging.LogUtils;
import com.ninni.minestrel.client.ClientProxy;
import com.ninni.minestrel.registry.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Minestrel.MODID)
public class Minestrel {
    public static final String MODID = "minestrel";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static CommonProxy PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);

    public Minestrel() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        MBlockRegistry.DEF_REG.register(modEventBus);
        MItemRegistry.DEF_REG.register(modEventBus);
        MBlockEntityRegistry.DEF_REG.register(modEventBus);
        MCreativeModeTabRegistry.DEF_REG.register(modEventBus);
        MMenuRegistry.DEF_REG.register(modEventBus);
        PROXY.init();
    }

    public void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> PROXY.commonSetup());
    }

    public void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> PROXY.clientSetup());
    }

}
