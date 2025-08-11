package ru.ycoord.examples.guis;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
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

public class ExampleSharedGuiSlot extends GuiBase {
    public ExampleSharedGuiSlot(ConfigurationSection section) {
        super(section);
    }

    private static final ConcurrentHashMap<Integer, ItemStack> items = new ConcurrentHashMap<>();

    private CompletableFuture<Void> saveAsync(Player player, Inventory inventory) {

        return CompletableFuture.runAsync(() -> {
            TransactionManager.lock("SHARED", "SAVE_SLOT_DATA");
            save(player, inventory);
            this.rebuild(player, false);
            TransactionManager.unlock("SHARED", "SAVE_SLOT_DATA");
        });
    }

    private void save(Player player, Inventory inventory) {
        for (Integer slot : slots.keySet()) {
            GuiItem item = slots.get(slot);
            if (item instanceof GuiSlot) {

                ItemStack v = inventory.getItem(slot);
                if (v == null)
                    items.remove(slot);
                else
                    items.put(slot, v);
                //try {
                //    Thread.sleep(100);
                //} catch (InterruptedException ignored) {
//
                //}
            }
        }
    }

    @Override
    public void handleClick(Player clicker, InventoryClickEvent e) {
        //if (TransactionManager.inProgress("SHARED", "SAVE_SLOT_DATA")) {
        //    e.setCancelled(true);
        //    ChatMessage message = YcoordCore.getInstance().getChatMessage();
        //    message.sendMessageIdAsync(MessageBase.Level.INFO, clicker, "data-is-loading");
//
        //    return;
        //}
//
        //super.handleClick(clicker, e);
        //saveAsync(clicker.getPlayer(), this.getInventory());
    }

    //@Override
    //public void handleClickInventory(Player clicker, InventoryClickEvent e) {
    //    if (TransactionManager.inProgress("SHARED", "SAVE_SLOT_DATA")) {
    //        e.setCancelled(true);
    //        ChatMessage message = YcoordCore.getInstance().getChatMessage();
    //        message.sendMessageIdAsync(MessageBase.Level.INFO, clicker, "data-is-loading");
//
    //        return;
    //    }
//
    //    super.handleClickInventory(clicker, e);
    //    saveAsync(clicker.getPlayer(), this.getInventory());
    //}

    @Override
    public void update(long elapsed, Player player) {
        if (!TransactionManager.inProgress("SHARED", "SAVE_SLOT_DATA")) {
            saveAsync(player, this.getInventory()).thenAccept(result -> {
                super.update(elapsed, player);
            });
        }
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
                        if (items.containsKey(slotIndex)) {
                            return items.get(slotIndex);
                        }
                        return null;
                    }
                }, priority, slotIndex, currentIndex, section);
            }
        }

        return new GuiItem(priority, slotIndex, currentIndex, section);
    }

    //@Override
    //public void onClose(InventoryCloseEvent event) {
    //    super.onClose(event);
    //    //if (event.getPlayer() instanceof Player player) {
    //    //    saveAsync(player, getInventory());
    //    //}
    //}

    //@Override
    //public void open(OfflinePlayer player) {
    //    super.open(player);
    //}
}
