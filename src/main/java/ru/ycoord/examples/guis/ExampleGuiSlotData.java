package ru.ycoord.examples.guis;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.ycoord.YcoordCore;
import ru.ycoord.core.gui.GuiBase;
import ru.ycoord.core.gui.items.GuiItem;
import ru.ycoord.core.gui.items.GuiSlot;
import ru.ycoord.core.messages.ChatMessage;
import ru.ycoord.core.messages.MessageBase;
import ru.ycoord.core.transaction.TransactionManager;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ExampleGuiSlotData extends GuiBase {
    public ExampleGuiSlotData(ConfigurationSection section) {
        super(section);
    }

    private static final ConcurrentHashMap<String, ConcurrentHashMap<Integer, ItemStack>> items = new ConcurrentHashMap<>();

    private void saveAsync(Player player, Inventory inventory) {
        CompletableFuture.runAsync(() -> {
            TransactionManager.lock(player.getName(), "SAVE_SLOT_DATA");
            for (Integer slot : slots.keySet()) {
                GuiItem item = slots.get(slot);
                if (item instanceof GuiSlot) {
                    ConcurrentHashMap<Integer, ItemStack> value = items.computeIfAbsent(player.getName(), k -> new ConcurrentHashMap<>());
                    ItemStack v = inventory.getItem(slot);
                    if (v == null)
                        value.remove(slot);
                    else
                        value.put(slot, v);

                    try {
                        Thread.sleep(500);
                    } catch (Exception ex) {
                    }
                }
            }
            TransactionManager.unlock(player.getName(), "SAVE_SLOT_DATA");
        });
    }

    public GuiItem makeItem(int currentIndex, int slotIndex, int priority, OfflinePlayer player, String type, ConfigurationSection section) {
        Player onlinePlayer = player.getPlayer();
        if (onlinePlayer != null) {
            if (type.equalsIgnoreCase("MARKER")) {
                return new GuiSlot(new GuiSlot.IItemProvider() {


                    @Override
                    public void saveAll(Player clicker, Inventory inventory) {
                        saveAsync(clicker, inventory);
                    }

                    @Override
                    public ItemStack getItem() {
                        if (items.containsKey(onlinePlayer.getName())) {
                            ConcurrentHashMap<Integer, ItemStack> slots = items.get(onlinePlayer.getName());
                            if (slots.containsKey(slotIndex)) {
                                return slots.get(slotIndex);
                            }
                        }
                        return null;
                    }
                }, priority, slotIndex, currentIndex, section);
            }
        }

        return new GuiItem(priority, slotIndex, currentIndex, section);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        super.onClose(event);
        if (event.getPlayer() instanceof Player player) {
            saveAsync(player, getInventory());
        }
    }

    @Override
    public void open(OfflinePlayer player) {
        if (TransactionManager.inProgress(player.getName(), "SAVE_SLOT_DATA")) {

            ChatMessage message = YcoordCore.getInstance().getChatMessage();
            message.sendMessageIdAsync(MessageBase.Level.INFO, player, "data-is-loading");

            return;
        }
        super.open(player);
    }
}
