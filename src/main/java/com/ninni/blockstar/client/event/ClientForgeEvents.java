package com.ninni.blockstar.client.event;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.client.midi.MidiSettingsConfig;
import com.ninni.blockstar.client.midi.MidiSettingsScreen;
import com.ninni.blockstar.server.block.RodType;
import com.ninni.blockstar.server.item.MetronomeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.SoundOptionsScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid = Blockstar.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientForgeEvents {
    private static final Set<UUID> activeUUIDs = new HashSet<>();
    private static final Map<UUID, Integer> beatCounters = new HashMap<>();
    private static final Map<UUID, Boolean> swingPhase = new HashMap<>();
    private static final Map<UUID, Long> lastTickPhaseTime = new HashMap<>();
    private static final Map<UUID, RodType> itemRodState = new HashMap<>();

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init event) {
        if (event.getScreen() instanceof SoundOptionsScreen screen) {
            int x = MidiSettingsConfig.parse(MidiSettingsConfig.buttonX.replace("width", String.valueOf(screen.width)));
            int y = MidiSettingsConfig.parse(MidiSettingsConfig.buttonY.replace("height", String.valueOf(screen.height)));

            Button button1 = Button.builder(Component.translatable("blockstar.options.midi.title"), (button) ->
                            Minecraft.getInstance().setScreen(new MidiSettingsScreen(screen, screen.options))).bounds(x, y, 80, 20).build();

            screen.children.add(button1);
            screen.narratables.add(button1);
            screen.renderables.add(button1);
        }
    }


    @SubscribeEvent
    public static void onRightClickItemInInventory(ScreenEvent.MouseButtonPressed.Pre event) {

        if (event.getScreen() instanceof AbstractContainerScreen<?> screen) {

            double mouseX = event.getMouseX();
            double mouseY = event.getMouseY();

            for (Slot slot : screen.getMenu().slots) {
                if (slot.isActive() && slot.hasItem()) {

                    if (isPointInRegion(screen, slot.x, slot.y, 16, 16, mouseX, mouseY)) {
                        ItemStack stack = slot.getItem();
                        if (stack.getItem() instanceof MetronomeItem && event.getButton() == 1) {
                            boolean current = MetronomeItem.isActive(stack);
                            MetronomeItem.setActive(stack, !current);
                            Minecraft.getInstance().player.playNotifySound(SoundEvents.UI_BUTTON_CLICK.get(), SoundSource.MASTER, 0.15F, current ? 0.8f : 1.2f);
                            event.setCanceled(true);
                            return;
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || Minecraft.getInstance().isPaused()) return;

        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        long now = System.currentTimeMillis();

        for (ItemStack stack : player.getInventory().items) {
            tickMetronome(stack, player, now);
        }

        if (Minecraft.getInstance().screen instanceof AbstractContainerScreen<?> screen) {
            for (Slot slot : screen.getMenu().slots) {
                tickMetronome(slot.getItem(), player, now);
            }
        }

        beatCounters.keySet().removeIf(uuid -> !activeUUIDs.contains(uuid));
        swingPhase.keySet().removeIf(uuid -> !activeUUIDs.contains(uuid));
        lastTickPhaseTime.keySet().removeIf(uuid -> !activeUUIDs.contains(uuid));
        itemRodState.keySet().removeIf(uuid -> !activeUUIDs.contains(uuid));
        activeUUIDs.clear();
    }

    private static void tickMetronome(ItemStack stack, Player player, long now) {
        if (!(stack.getItem() instanceof MetronomeItem) || !MetronomeItem.isActive(stack)) return;

        int bpm = MetronomeItem.getBPM(stack);
        long interval = 60000L / bpm / 2;
        UUID id = MetronomeItem.getOrCreateUniqueID(stack);

        activeUUIDs.add(id);

        long lastTick = lastTickPhaseTime.getOrDefault(id, 0L);
        boolean isSwing = swingPhase.getOrDefault(id, true);

        if (now - lastTick >= interval) {
            lastTickPhaseTime.put(id, now);

            RodType rod;
            if (!isSwing) {
                rod = RodType.MIDDLE;
            } else {
                int beat = getCurrentBeat(id);
                int beatsPerMeasure = MetronomeItem.getTimeSigValues(MetronomeItem.getTimeSig(stack), true);
                boolean isDownbeat = (beat % beatsPerMeasure) == 0;

                rod = (beat % 2 == 0) ? RodType.LEFT : RodType.RIGHT;

                player.playSound(
                        isDownbeat ? SoundEvents.NOTE_BLOCK_BASEDRUM.value() : SoundEvents.NOTE_BLOCK_HAT.value(),
                        0.5F,
                        isDownbeat ? 1.0F : 1.2F
                );

                beatCounters.put(id, (beat + 1) % beatsPerMeasure);
            }

            itemRodState.put(id, rod);
            swingPhase.put(id, !isSwing);
        }
    }

    public static RodType getItemRod(UUID id) {
        return itemRodState.getOrDefault(id, RodType.MIDDLE);
    }

    public static int getCurrentBeat(UUID id) {
        return beatCounters.getOrDefault(id, 0);
    }

    private static boolean isPointInRegion(AbstractContainerScreen<?> screen, int x, int y, int width, int height, double mouseX, double mouseY) {
        int guiLeft = screen.getGuiLeft();
        int guiTop = screen.getGuiTop();
        mouseX -= guiLeft;
        mouseY -= guiTop;
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }
}
