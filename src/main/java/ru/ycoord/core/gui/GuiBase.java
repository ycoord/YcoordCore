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
import ru.ycoord.core.gui.items.GuiBackButton;
import ru.ycoord.core.gui.items.GuiItem;
import ru.ycoord.core.gui.items.GuiViewerHeadItem;
import ru.ycoord.core.messages.ChatMessage;
import ru.ycoord.core.messages.MessageBase;
import ru.ycoord.core.messages.MessagePlaceholders;
import ru.ycoord.core.sound.SoundInfo;
import ru.ycoord.core.transaction.TransactionManager;

import java.util.*;

public class GuiBase implements InventoryHolder {
    protected final ConfigurationSection section;
    private Inventory inventory = null;
    protected HashMap<Integer, List<GuiItemCharacter>> items = new HashMap<>();
    protected HashMap<Integer, GuiItem> slots = new HashMap<>();
    protected HashMap<String, Integer> typeCounter = new HashMap<>();
    private Animation animation = null;
    private boolean animateOnlyOnOpen = false;
    private SoundInfo openSound = null;
    private SoundInfo closeSound = null;
    private boolean lockOnAnimation;

    public GuiBase(ConfigurationSection section) {
        this.section = section;
        ConfigurationSection animationSection = section.getConfigurationSection("animation");
        if (animationSection != null) {
            lockOnAnimation = section.getBoolean("lock", false);

            this.animateOnlyOnOpen = animationSection.getBoolean("only-on-open");
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
        } else {
            animation = new NoAnimation(null);
        }

        ConfigurationSection openSound = section.getConfigurationSection("open-sound");
        if (openSound != null) {
            this.openSound = new SoundInfo(openSound);
        }

        ConfigurationSection closeSound = section.getConfigurationSection("close-sound");
        if (closeSound != null) {
            this.closeSound = new SoundInfo(closeSound);
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public void open(OfflinePlayer player) {
        int size = section.getInt("size", 54);
        String title = section.getString("title", "Заголовок");

        MessagePlaceholders placeholders = new MessagePlaceholders(player);
        getExtraPlaceholders(placeholders);

        title = MessageBase.translateColor(MessageBase.Level.NONE, title, placeholders);

        inventory = Bukkit.createInventory(this, size, Component.text(title));
        rebuild(player);
        Objects.requireNonNull(player.getPlayer()).openInventory(inventory);
        if (openSound != null)
            openSound.play(player);
    }

    public void onClose(InventoryCloseEvent event) {


        if (closeSound != null) {
            if (event.getPlayer() instanceof Player player) {
                if (lockOnAnimation && TransactionManager.inProgress(player.getName(), this.getClass().getSimpleName())) {
                    TransactionManager.unlock(player.getName(), this.getClass().getSimpleName());
                    return;
                }
                closeSound.play(player);
            }
        }
    }

    public void update(long elapsed, Player player) {
        MessagePlaceholders placeholders = new MessagePlaceholders(player);
        getExtraPlaceholders(placeholders);
        for (Integer slot : items.keySet()) {
            List<GuiItemCharacter> guiItems = items.get(slot);

            for (int i = 0; i < guiItems.size(); i++) {
                GuiItemCharacter guiItem = guiItems.get(i);
                if (guiItem.item != null)
                    guiItem.item.update(this, slot, guiItem.index, elapsed, player, placeholders);
            }
        }
    }

    public void getExtraPlaceholders(MessagePlaceholders placeholders) {

    }

    public static class GuiItemCharacter {
        public int slot;
        public int index;
        public GuiItem item;
        public Character character;
        public String type;

        public GuiItemCharacter(int index, int slot, String type, GuiItem item, Character character) {
            this.index = index;
            this.slot = slot;
            this.item = item;
            this.character = character;
            this.type = type;
        }
    }

    protected void refresh(OfflinePlayer player, HashMap<Integer, List<GuiItemCharacter>> guiElements) {
        MessagePlaceholders placeholders = new MessagePlaceholders(player);
        getExtraPlaceholders(placeholders);
        if (player.getPlayer() != null) {
            animation.animate(this, inventory, player.getPlayer(), guiElements, placeholders);
            if (animateOnlyOnOpen) {
                animateOnlyOnOpen = false;
                animation = new NoAnimation(null);
            }
        }

    }

    public void rebuild(OfflinePlayer player) {
        typeCounter.clear();
        Bukkit.getScheduler().runTaskAsynchronously(YcoordCore.getInstance(), () -> {
            if(lockOnAnimation)
                TransactionManager.lock(player.getName(), this.getClass().getSimpleName());
            inventory.clear();
            this.items = make(player, section);
            refresh(player, items);
            if(lockOnAnimation)
                TransactionManager.unlock(player.getName(), this.getClass().getSimpleName());
        });
    }

    public void setSlotItem(int slot, int index, GuiItem guiItem, Player player, MessagePlaceholders messagePlaceholders) {
        GuiItem itemInSlot = slots.get(slot);
        boolean onlyUpdateMeta = (itemInSlot == guiItem) && guiItem.isRedraw();
        ItemStack itemStack = guiItem.buildItem(player, this, slot, index, messagePlaceholders, onlyUpdateMeta);
        //if (itemStack == null)
        //    return;
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

                    int currentIndex = typeCounter.getOrDefault(type, 0);
                    guiElements.computeIfAbsent(slotIndex, k -> new LinkedList<>())
                            .add(new GuiItemCharacter(currentIndex, slotIndex, type, makeItem(currentIndex, slotIndex, priority, player, type, itemSection), c));
                    typeCounter.put(type, currentIndex + 1);
                    priority++;
                }

                if (!found) {
                    int currentIndex = typeCounter.getOrDefault("ITEM", 0);
                    GuiItem item = YcoordCore.getInstance().getGuiManager().getGlobalItem(stringC, slotIndex, currentIndex);
                    if (item == null)
                        continue;

                    guiElements.computeIfAbsent(slotIndex, k -> new LinkedList<>())
                            .add(new GuiItemCharacter(currentIndex, slotIndex, "ITEM", item, c));
                    typeCounter.put("ITEM", currentIndex + 1);
                }

            }
        }
        return guiElements;
    }


    public GuiItem makeItem(int currentIndex, int slotIndex, int priority, OfflinePlayer player, String type, ConfigurationSection section) {
        Player onlinePlayer = player.getPlayer();
        if (onlinePlayer != null) {
            if (type.equalsIgnoreCase("VIEWER_HEAD")) {
                return new GuiViewerHeadItem(onlinePlayer.getName(), priority, slotIndex, currentIndex, section);
            }
            if (type.equalsIgnoreCase("BACK")) {
                return new GuiBackButton(priority, slotIndex, currentIndex, section);
            }
        }

        return new GuiItem(priority, slotIndex, currentIndex, section);
    }


    public void handleClick(Player clicker, InventoryClickEvent e) {

        if (lockOnAnimation && TransactionManager.inProgress(clicker.getName(), this.getClass().getSimpleName())) {
            e.setCancelled(true);
            ChatMessage chat = YcoordCore.getInstance().getChatMessage();
            chat.sendMessageId(MessageBase.Level.INFO, clicker, "animation-in-progress");
            return;
        }
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
