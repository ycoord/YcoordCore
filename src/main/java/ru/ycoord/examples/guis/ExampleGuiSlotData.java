package ru.ycoord.examples.guis;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
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

    protected String getKey(OfflinePlayer player) {
        return "HELLO";
    }

    protected String getValue(OfflinePlayer player) {
        return "SAVE_SLOT_DATA";
    }

    protected void setItem(OfflinePlayer player, int slot, Inventory inventory, ItemStack item) {
        ConcurrentHashMap<Integer, ItemStack> value = items.computeIfAbsent(getKey(player), k -> new ConcurrentHashMap<>());
        ItemStack v = inventory.getItem(slot);
        if (v == null)
            value.remove(slot);
        else
            value.put(slot, v);
    }

    protected ItemStack getItem(OfflinePlayer player, int slot) {
        if (items.containsKey(getKey(player))) {
            ConcurrentHashMap<Integer, ItemStack> slots = items.get(getKey(player));
            if (slots.containsKey(slot)) {
                return slots.get(slot);
            }
        }
        return null;
    }

    private void saveAsync(OfflinePlayer player, Inventory inventory) {
        TransactionManager.lock(getKey(player), getValue(player));
        Bukkit.getScheduler().runTaskLaterAsynchronously(YcoordCore.getInstance(), (task) -> {
            for (Integer slot : slots.keySet()) {
                GuiItem item = slots.get(slot);
                if (item instanceof GuiSlot) {
                    ItemStack v = inventory.getItem(slot);
                    setItem(player, slot, inventory, v);
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

            TransactionManager.unlock(getKey(player), getValue(player));
        }, 1);
    }

    public GuiItem makeItem(int currentIndex, int slotIndex, int priority, OfflinePlayer player, String type, ConfigurationSection section) {
        Player onlinePlayer = player.getPlayer();
        if (onlinePlayer != null) {
            if (type.equalsIgnoreCase("MARKER")) {
                return new GuiSlot((player1) -> getItem(player1, slotIndex), priority, slotIndex, currentIndex, section);
            }
        }

        return new GuiItem(priority, slotIndex, currentIndex, section);
    }


    boolean canCanProcess(OfflinePlayer clicker) {
        if (TransactionManager.inProgress(getKey(clicker), getValue(clicker))) {
            ChatMessage message = YcoordCore.getInstance().getChatMessage();
            message.sendMessageIdAsync(MessageBase.Level.INFO, clicker, "data-is-loading");
            return false;
        }
        return true;
    }

    boolean handleEvent(OfflinePlayer clicker, InventoryInteractEvent e) {
        if (!canCanProcess(clicker)) {
            e.setCancelled(true);
            return false;
        }
        saveAsync(clicker, e.getInventory());
        return true;
    }

    @Override
    public void handleClickInventory(Player clicker, InventoryClickEvent e) {
        super.handleClickInventory(clicker, e);
        if (e.isCancelled())
            return;
        handleEvent(clicker, e);
    }

    @Override
    public void handleClick(Player clicker, InventoryClickEvent e) {
        super.handleClick(clicker, e);
        if (e.isCancelled())
            return;
        handleEvent(clicker, e);
    }

    @Override
    public void handleDrag(Player clicker, InventoryDragEvent e) {
        super.handleDrag(clicker, e);
        if (e.isCancelled())
            return;
        handleEvent(clicker, e);
    }

    @Override
    public void open(OfflinePlayer player) {
        if (!canCanProcess(player)) {
            return;
        }
        super.open(player);
    }
}
