package com.ninni.blockstar.client.event;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.client.midi.MidiSettingsConfig;
import com.ninni.blockstar.client.midi.MidiSettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.SoundOptionsScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Blockstar.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientForgeEvents {

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init event) {
        if (event.getScreen() instanceof SoundOptionsScreen screen) {

            int x = MidiSettingsConfig.parse(MidiSettingsConfig.buttonX.replace("width", String.valueOf(screen.width)));
            int y = MidiSettingsConfig.parse(MidiSettingsConfig.buttonY.replace("height", String.valueOf(screen.height)));


            Button button1 = Button.builder(Component.translatable("blockstar.options.midi.title"), (button) ->
                            Minecraft.getInstance().setScreen(new MidiSettingsScreen(screen, screen.options)))
                    .bounds(x, y, 80, 20)
                    .build();

            screen.children.add(button1);
            screen.narratables.add(button1);
            screen.renderables.add(button1);
        }
    }
}
