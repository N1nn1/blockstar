package com.ninni.blockstar;

import com.mojang.logging.LogUtils;
import com.ninni.blockstar.client.ClientProxy;
import com.ninni.blockstar.registry.*;
import com.ninni.blockstar.server.event.CommonEvents;
import com.ninni.blockstar.server.instrument.InstrumentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Mod(Blockstar.MODID)
public class Blockstar {
    public static final String MODID = "blockstar";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final List<Runnable> CALLBACKS = new ArrayList<>();
    public static CommonProxy PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);

    public Blockstar() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::buildRegistries);
        BBlockRegistry.DEF_REG.register(modEventBus);
        BItemRegistry.DEF_REG.register(modEventBus);
        BBlockEntityRegistry.DEF_REG.register(modEventBus);
        BCreativeModeTabRegistry.DEF_REG.register(modEventBus);
        BMenuRegistry.DEF_REG.register(modEventBus);
        BInstrumentTypeRegistry.DEF_REG.register(modEventBus);
        BRecipeRegistry.DEF_REG_SERIALIZERS.register(modEventBus);
        BRecipeRegistry.DEF_REG_TYPES.register(modEventBus);
        BNetwork.register();
        PROXY.init();
        MinecraftForge.EVENT_BUS.register(new CommonEvents());
    }

    public void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> PROXY.commonSetup());
    }

    public void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> PROXY.clientSetup());
    }

    private void buildRegistries(NewRegistryEvent event) {
        RegistryBuilder<InstrumentType> instrumentTypeRegistryBuilder = new RegistryBuilder<InstrumentType>().setName(new ResourceLocation(MODID, "instrument_type")).setDefaultKey(new ResourceLocation(Blockstar.MODID, "keyboard"));
        event.create(instrumentTypeRegistryBuilder, BInstrumentTypeRegistry::setInternalRegistry);
    }

}
