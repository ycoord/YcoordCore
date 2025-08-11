package ru.ycoord.examples.guis;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import ru.ycoord.YcoordCore;
import ru.ycoord.core.gui.GuiBase;
import ru.ycoord.core.gui.items.GuiItem;
import ru.ycoord.core.gui.items.GuiSlot;
import ru.ycoord.core.messages.ChatMessage;
import ru.ycoord.core.messages.MessageBase;
import ru.ycoord.core.transaction.TransactionManager;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ExampleGuiSlotData extends GuiBase {
    public ExampleGuiSlotData(ConfigurationSection section) {
        super(section);
    }

    private static final ConcurrentHashMap<String, ConcurrentHashMap<Integer, ItemStack>> items = new ConcurrentHashMap<>();

    private void saveAsync(Player player, Inventory inventory) {
        TransactionManager.lock("HELLO", "SAVE_SLOT_DATA");
        Bukkit.getScheduler().runTaskLaterAsynchronously(YcoordCore.getInstance(), (task)->{
            for (Integer slot : slots.keySet()) {
                GuiItem item = slots.get(slot);
                if (item instanceof GuiSlot) {
                    ConcurrentHashMap<Integer, ItemStack> value = items.computeIfAbsent("HELLO", k -> new ConcurrentHashMap<>());
                    ItemStack v = inventory.getItem(slot);
                    if (v == null)
                        value.remove(slot);
                    else
                        value.put(slot, v);
                }
            }



            List<? extends Player> players = Bukkit.getOnlinePlayers().stream().filter(p -> p.getPlayer() != player).toList();

            for (Player p : players) {
                InventoryView view = p.getOpenInventory();
                Inventory top = view.getTopInventory();
                if (top.getHolder() instanceof ExampleGuiSlotData g) {
                    g.rebuild(p, false);
                }
            }

            TransactionManager.unlock("HELLO", "SAVE_SLOT_DATA");
        }, 1);
    }

    public GuiItem makeItem(int currentIndex, int slotIndex, int priority, OfflinePlayer player, String type, ConfigurationSection section) {
        Player onlinePlayer = player.getPlayer();
        if (onlinePlayer != null) {
            if (type.equalsIgnoreCase("MARKER")) {
                return new GuiSlot(() -> {
                    if (items.containsKey("HELLO")) {
                        ConcurrentHashMap<Integer, ItemStack> slots = items.get("HELLO");
                        if (slots.containsKey(slotIndex)) {
                            return slots.get(slotIndex);
                        }
                    }
                    return null;
                }, priority, slotIndex, currentIndex, section);
            }
        }

        return new GuiItem(priority, slotIndex, currentIndex, section);
    }


    @Override
    public void handleClickInventory(Player clicker, InventoryClickEvent e) {
        super.handleClickInventory(clicker, e);
        if (TransactionManager.inProgress("HELLO", "SAVE_SLOT_DATA")) {
            e.setCancelled(true);
            return;
        }

        saveAsync(clicker, e.getInventory());
    }

    @Override
    public void handleClick(Player clicker, InventoryClickEvent e) {
        super.handleClick(clicker, e);
        if (e.isCancelled())
            return;

        GuiItem clicked = this.slots.getOrDefault(e.getSlot(), null);
        if (clicked == null)
            return;

        if (TransactionManager.inProgress("HELLO", "SAVE_SLOT_DATA")) {
            e.setCancelled(true);
            ChatMessage message = YcoordCore.getInstance().getChatMessage();
            message.sendMessageIdAsync(MessageBase.Level.INFO, clicker, "data-is-loading");
            return;
        }
        saveAsync(clicker, e.getInventory());
    }

    @Override
    public void handleDrag(Player clicker, InventoryDragEvent e) {
        super.handleDrag(clicker, e);
        if (TransactionManager.inProgress("HELLO", "SAVE_SLOT_DATA")) {
            e.setCancelled(true);
            return;
        }

        saveAsync(clicker, e.getInventory());
    }

    @Override
    public void open(OfflinePlayer player) {
        if (TransactionManager.inProgress("HELLO", "SAVE_SLOT_DATA")) {

            ChatMessage message = YcoordCore.getInstance().getChatMessage();
            message.sendMessageIdAsync(MessageBase.Level.INFO, player, "data-is-loading");

            return;
        }
        super.open(player);
    }
}
