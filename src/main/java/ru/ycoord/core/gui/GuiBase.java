package ru.ycoord.core.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import ru.ycoord.YcoordCore;
import ru.ycoord.core.gui.animation.*;
import ru.ycoord.core.gui.items.GuiItem;
import ru.ycoord.core.gui.items.GuiViewerHeadItem;
import ru.ycoord.core.messages.MessageBase;
import ru.ycoord.core.messages.MessagePlaceholders;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class GuiBase implements InventoryHolder {
    private final ConfigurationSection section;
    private Inventory inventory = null;
    private HashMap<Integer, GuiItemCharacter> items = new HashMap<Integer, GuiItemCharacter>();
    private Animation animation = null;

    public GuiBase(ConfigurationSection section) {
        this.section = section;

        ConfigurationSection animationSection = section.getConfigurationSection("animation");
        if (animationSection != null) {
            String type = animationSection.getString("type");
            if (type != null) {
                if (type.equalsIgnoreCase("CORNER")) {
                    animation = new CornerAnimation(animationSection);
                } else if (type.equalsIgnoreCase("DIAGONAL")) {
                    animation = new DiagonalAnimation(animationSection);
                } else if (type.equalsIgnoreCase("HORIZONTAL")) {
                    animation = new HorizontalAnimation(animationSection);
                } else if (type.equalsIgnoreCase("VERTICAL")) {
                    animation = new VerticalAnimation(animationSection);
                } else if (type.equalsIgnoreCase("SPIRAL")) {
                    animation = new SpiralAnimation(animationSection);
                } else if (type.equalsIgnoreCase("STEP")) {
                    animation = new StepByStep(animationSection);
                } else {
                    animation = new NoAnimation(animationSection);
                }
            }
        }
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
            if (item.item != null)
                item.item.update(this, elapsed, player);
        }
    }

    public static class GuiItemCharacter {
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

        if (player.getPlayer() != null)
            animation.animate(this, inventory, player.getPlayer(), guiElements, messagePlaceholders);

    }

    public void rebuild(OfflinePlayer player) {
        inventory.clear();
        Bukkit.getScheduler().runTaskAsynchronously(YcoordCore.getInstance(), () -> {
            this.items = make(player, section);
            refresh(player, items);
        });
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

                    String type = itemSection.getString("type", null);
                    if (type == null)
                        continue;

                    found = true;
                    guiElements.put(slotIndex, new GuiItemCharacter(makeItem(player, type, itemSection), c));
                }

                if (!found) {
                    GuiItem item = YcoordCore.getInstance().getGuiManager().getGlobalItem(stringC);
                    if (item == null)
                        continue;
                    guiElements.put(slotIndex, new GuiItemCharacter(item, c));
                }
            }
        }
        return guiElements;
    }

    public GuiItem makeItem(OfflinePlayer player, String type, ConfigurationSection section) {
        Player onlinePlayer = player.getPlayer();
        if (onlinePlayer != null) {
            if (type.equalsIgnoreCase("VIEWER_HEAD")) {
                return new GuiViewerHeadItem(onlinePlayer.getName(), section);
            }
        }

        return new GuiItem(section);
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
