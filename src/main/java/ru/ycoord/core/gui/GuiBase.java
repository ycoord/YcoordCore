package ru.ycoord.core.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ru.ycoord.YcoordCore;
import ru.ycoord.core.gui.items.GuiItem;
import ru.ycoord.core.messages.MessageBase;
import ru.ycoord.core.messages.MessagePlaceholders;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class GuiBase implements InventoryHolder {
    private ConfigurationSection section;
    private Inventory inventory = null;
    private HashMap<Integer, GuiItemCharacter> items = new  HashMap<Integer, GuiItemCharacter>();

    public GuiBase(ConfigurationSection section) {
        this.section = section;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public void open(OfflinePlayer player) {
        int size = section.getInt("size", 54);
        String title = section.getString("title", "title");

        title = MessageBase.translateColor(title, new MessagePlaceholders(player));

        inventory = Bukkit.createInventory(this, size, Component.text(title));
        rebuild(player);
        Objects.requireNonNull(player.getPlayer()).openInventory(inventory);
    }

    public void onClose(InventoryCloseEvent event) {
    }

    public void update(long elapsed, Player player) {
        for (GuiItemCharacter item : this.items.values()) {
                if(item.item != null)
                    item.item.update(this, elapsed, player);
            }
    }

    protected static class GuiItemCharacter {
        public GuiItem item;
        public Character character;

        public GuiItemCharacter(GuiItem item, Character character) {
            this.item = item;
            this.character = character;
        }
    }

    protected void refresh(OfflinePlayer player, HashMap<Integer, GuiItemCharacter> guiElements) {
        inventory.clear();
        MessagePlaceholders messagePlaceholders = new MessagePlaceholders(player);

        for (Integer slot : guiElements.keySet()) {
            if (guiElements.get(slot).item == null)
                continue;
            ItemStack i = guiElements.get(slot).item.buildItem(player, this, slot, messagePlaceholders);
            if (i == null)
                continue;
            inventory.setItem(slot, i);
        }

    }

    public void rebuild(OfflinePlayer player) {
        inventory.clear();
        this.items = make(player, section);
        refresh(player, items);
    }


    protected HashMap<Integer, GuiItemCharacter> make(OfflinePlayer player, ConfigurationSection section) {
        HashMap<Integer, GuiItemCharacter> guiElements = new HashMap<>();

        List<String> pattern = section.getStringList("pattern");
        ConfigurationSection items = section.getConfigurationSection("items");


        for (int i = 0; i < pattern.size(); i++) {
            for (int j = 0; j < pattern.get(i).length(); j++) {
                char c = pattern.get(i).charAt(j);
                String stringC = String.valueOf(c);
                int slotIndex = i * pattern.get(i).length() + j;

                assert items != null;

                Set<String> keys = items.getKeys(false);
                boolean found = false;
                for (String key : keys) {
                    ConfigurationSection itemSection = items.getConfigurationSection(key);
                    if (itemSection == null)
                        continue;
                    String symbol = itemSection.getString("symbol", null);
                    if (symbol == null)
                        continue;

                    if (!symbol.equalsIgnoreCase(stringC))
                        continue;

                    found = true;
                    guiElements.put(slotIndex, new GuiItemCharacter(new GuiItem(itemSection), c));
                }

                if (!found) {
                    GuiItem item = YcoordCore.getInstance().getGuiManager().getGlobalItem(stringC);
                    if (item == null)
                        continue;
                    guiElements.put(slotIndex, new GuiItemCharacter(
                            item,
                            c
                    ));
                }
            }
        }
        return guiElements;
    }

    public void handleClick(Player clicker, InventoryClickEvent e) {
        int slot = e.getSlot();
        if (!this.items.containsKey(slot))
            return;
        GuiItem i = this.items.get(slot).item;
        if (i == null)
            return;

        i.handleClick(this, e);
    }

    public void handleClickInventory(Player clicker, InventoryClickEvent e) {
        //int slot = e.getSlot();
        //if (!guiElements.containsKey(slot))
        //    return;
        //GuiItem i = guiElements.get(slot).item;
        //if (i == null)
        //    return;
        //
        //i.handleClick(this, clicker, e);
    }
}
