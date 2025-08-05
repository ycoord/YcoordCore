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
import ru.ycoord.core.gui.animation.*;
import ru.ycoord.core.gui.items.GuiItem;
import ru.ycoord.core.gui.items.GuiViewerHeadItem;
import ru.ycoord.core.messages.MessageBase;
import ru.ycoord.core.messages.MessagePlaceholders;

import java.util.*;

public class GuiBase implements InventoryHolder {
    private final ConfigurationSection section;
    private Inventory inventory = null;
    private HashMap<Integer, List<GuiItemCharacter>> items = new HashMap<>();
    private HashMap<Integer, GuiItem> slots = new HashMap<>();
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

        MessagePlaceholders placeholders = new MessagePlaceholders(player);
        getExtraPlaceholders(placeholders);

        title = MessageBase.translateColor(title, placeholders);

        inventory = Bukkit.createInventory(this, size, Component.text(title));
        rebuild(player);
        Objects.requireNonNull(player.getPlayer()).openInventory(inventory);
    }

    public void onClose(InventoryCloseEvent event) {
    }

    public void update(long elapsed, Player player) {
        MessagePlaceholders placeholders = new MessagePlaceholders(player);
        getExtraPlaceholders(placeholders);
        for (Integer slot : items.keySet()) {
            List<GuiItemCharacter> guiItems = items.get(slot);

            for (int i = 0; i < guiItems.size(); i++) {
                GuiItemCharacter guiItem = guiItems.get(i);
                if (guiItem.item != null)
                    guiItem.item.update(this, slot, elapsed, player, placeholders);
            }
        }
    }

    public void getExtraPlaceholders(MessagePlaceholders placeholders) {

    }

    public static class GuiItemCharacter {
        public GuiItem item;
        public Character character;

        public GuiItemCharacter(GuiItem item, Character character) {
            this.item = item;
            this.character = character;
        }
    }

    protected void refresh(OfflinePlayer player, HashMap<Integer, List<GuiItemCharacter>> guiElements) {
        inventory.clear();
        MessagePlaceholders placeholders = new MessagePlaceholders(player);
        getExtraPlaceholders(placeholders);
        if (player.getPlayer() != null)
            animation.animate(this, inventory, player.getPlayer(), guiElements, placeholders);

    }

    public void rebuild(OfflinePlayer player) {
        inventory.clear();
        Bukkit.getScheduler().runTaskAsynchronously(YcoordCore.getInstance(), () -> {
            this.items = make(player, section);
            refresh(player, items);
        });
    }

    public void setSlotItem(int slot, GuiItem guiItem, Player player, MessagePlaceholders messagePlaceholders) {
        ItemStack itemStack = guiItem.buildItem(player, this, slot, messagePlaceholders);
        if (itemStack == null)
            return;
        slots.put(slot, guiItem);
        inventory.setItem(slot, itemStack);
    }

    public void setSlotItemReady(int slot, GuiItem guiItem, ItemStack itemStack) {
        if (itemStack == null)
            return;
        slots.put(slot, guiItem);
        inventory.setItem(slot, itemStack);
    }

    public HashMap<Integer, GuiItem> getSlots() {
        return slots;
    }

    protected HashMap<Integer, List<GuiItemCharacter>> make(OfflinePlayer player, ConfigurationSection section) {
        HashMap<Integer, List<GuiItemCharacter>> guiElements = new HashMap<>();

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
                int priority = 0;
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

                    guiElements.computeIfAbsent(slotIndex, k -> new LinkedList<>())
                            .add(new GuiItemCharacter(makeItem(priority, player, type, itemSection), c));
                    priority++;
                }

                if (!found) {
                    GuiItem item = YcoordCore.getInstance().getGuiManager().getGlobalItem(stringC);
                    if (item == null)
                        continue;
                    guiElements.computeIfAbsent(slotIndex, k -> new LinkedList<>())
                            .add(new GuiItemCharacter(item, c));
                }
            }
        }
        return guiElements;
    }

    public GuiItem makeItem(int priority, OfflinePlayer player, String type, ConfigurationSection section) {
        Player onlinePlayer = player.getPlayer();
        if (onlinePlayer != null) {
            if (type.equalsIgnoreCase("VIEWER_HEAD")) {
                return new GuiViewerHeadItem(onlinePlayer.getName(), priority, section);
            }
        }

        return new GuiItem(priority, section);
    }


    public void handleClick(Player clicker, InventoryClickEvent e) {
        int slot = e.getSlot();
        if (!this.slots.containsKey(slot))
            return;
        GuiItem i = this.slots.get(slot);
        if (i == null)
            return;
        if (e.getWhoClicked() instanceof Player player) {
            MessagePlaceholders placeholders = new MessagePlaceholders(player);
            getExtraPlaceholders(placeholders);
            i.handleClick(this, e, placeholders);
        }
    }

    public void handleClickInventory(Player clicker, InventoryClickEvent e) {

    }
}
