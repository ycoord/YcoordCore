package ru.ycoord.examples.guis;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.ycoord.core.gui.GuiBase;
import ru.ycoord.core.gui.items.GuiItem;
import ru.ycoord.core.gui.items.GuiSlot;

import java.util.concurrent.ConcurrentHashMap;

public class ExampleGuiSlotData extends GuiBase {
    public ExampleGuiSlotData(ConfigurationSection section) {
        super(section);
    }

    private static final ConcurrentHashMap<String, ConcurrentHashMap<Integer, ItemStack>> items = new ConcurrentHashMap<>();

    private void save(Player player, Inventory inventory) {
        for (Integer slot : slots.keySet()) {
            GuiItem item = slots.get(slot);
            if (item instanceof GuiSlot) {
                items.computeIfAbsent(player.getName(), k -> new ConcurrentHashMap<>());
                ConcurrentHashMap<Integer, ItemStack> value = items.get(player.getName());
                ItemStack v = inventory.getItem(slot);
                if (v == null)
                    value.remove(slot);
                else
                    value.put(slot, v);
            }
        }
    }

    public GuiItem makeItem(int currentIndex, int slotIndex, int priority, OfflinePlayer player, String type, ConfigurationSection section) {
        Player onlinePlayer = player.getPlayer();
        if (onlinePlayer != null) {
            if (type.equalsIgnoreCase("MARKER")) {
                return new GuiSlot(new GuiSlot.IItemProvider() {


                    @Override
                    public void saveAll(Player clicker, Inventory inventory) {
                        save(clicker, inventory);
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
            save(player, getInventory());
        }
    }
}
