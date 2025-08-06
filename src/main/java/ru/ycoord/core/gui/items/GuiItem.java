package ru.ycoord.core.gui.items;


import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.ycoord.YcoordCore;
import ru.ycoord.core.gui.GuiBase;
import ru.ycoord.core.gui.GuiManager;
import ru.ycoord.core.messages.ChatMessage;
import ru.ycoord.core.messages.MessageBase;
import ru.ycoord.core.messages.MessagePlaceholders;
import ru.ycoord.core.sound.SoundInfo;
import ru.ycoord.core.utils.Utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuiItem {
    protected final ConfigurationSection section;
    private SoundInfo sound = null;
    private int priority;
    private boolean redraw = false;
    protected final long current = System.currentTimeMillis();
    private List<String> lore = new LinkedList<>();
    private ItemStack stack;

    public GuiItem(int priority, ConfigurationSection section) {
        this.section = section;
        this.priority = priority;
        this.lore = section.getStringList("lore");
        this.redraw = section.getBoolean("redraw", false);
        ConfigurationSection soundSection = section.getConfigurationSection("sound");

        if (soundSection != null) {
            this.sound = new SoundInfo(soundSection);
        } else {
            ConfigurationSection config = YcoordCore.getInstance().getConfig();
            ConfigurationSection itemSettings = config.getConfigurationSection("gui-settings");
            if (itemSettings == null)
                return;

            ConfigurationSection soundSettings = itemSettings.getConfigurationSection("item-sound");
            if (soundSettings == null)
                return;


            boolean useDefaultSound = soundSettings.getBoolean("use-default", true);
            if (useDefaultSound) {
                ConfigurationSection defaultSoundSection = soundSettings.getConfigurationSection("default-sound");
                if (defaultSoundSection == null)
                    return;
                this.sound = new SoundInfo(defaultSoundSection);
            }
        }
    }

    protected List<String> getLoreBefore(OfflinePlayer player) {
        return new LinkedList<>();
    }

    protected List<String> getLoreAfter(OfflinePlayer player) {
        return new LinkedList<>();
    }

    public void apply(OfflinePlayer clicker, ItemStack stack, MessagePlaceholders placeholders) {
        List<String> loreBefore = getLoreBefore(clicker);

        List<String> loreAfter = getLoreAfter(clicker);

        List<String> resultLore = new LinkedList<>();
        for (String loreItem : loreBefore) {
            resultLore.add(MessageBase.translateColor(MessageBase.Level.NONE, loreItem, placeholders));
        }

        for (String loreItem : lore) {
            resultLore.add(MessageBase.translateColor(MessageBase.Level.NONE, loreItem, placeholders));
        }

        for (String loreItem : loreAfter) {
            resultLore.add(MessageBase.translateColor(MessageBase.Level.NONE, loreItem, placeholders));
        }

        ItemMeta meta = stack.getItemMeta();
        String name = section.getString("name", "имя");
        meta.displayName(Component.text(MessageBase.translateColor(MessageBase.Level.NONE, name, placeholders)));

        List<Component> components = new LinkedList<>();
        for (String loreItem : resultLore) {
            meta.lore(new LinkedList<>());
            components.add(Component.text(MessageBase.translateColor(MessageBase.Level.NONE, loreItem, placeholders)));
        }

        meta.lore(components);

        meta.setCustomModelData(999);
        stack.setItemMeta(meta);

        if (section.getBoolean("glow", false)) {
            stack.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
            ItemMeta meta2 = stack.getItemMeta();
            meta2.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            stack.setItemMeta(meta2);
        }

        stack.addItemFlags(ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_POTION_EFFECTS);
    }

    public ItemStack buildItem(OfflinePlayer clicker, GuiBase base, int slot, MessagePlaceholders placeholders, boolean onlyMeta) {
        if (!checkCondition(clicker.getPlayer()))
            return null;
        getExtraPlaceholders(placeholders, slot, base);
        if(!onlyMeta)
        {
            ItemStack stack;

            String texture = section.getString("texture", null);
            if (texture != null && !texture.isEmpty()) {
                Utils.createPlayerHeadBase64Async(texture).thenAccept(item -> {
                    apply(clicker, item, placeholders);
                    base.setSlotItemReady(slot, this, item);
                });
                stack = new ItemStack(Material.PLAYER_HEAD);

            } else {
                stack = new ItemStack(Material.valueOf(section.getString("material", "BARRIER")));
            }
            this.stack = stack;
        }
        apply(clicker, this.stack, placeholders);
        return this.stack;
    }

    private boolean checkCooldown(Player clicker) {
        HashMap<String, Long> cd = GuiManager.cooldowns;
        {
            long curr = System.currentTimeMillis();
            if (!cd.containsKey(clicker.getName())) {
                cd.put(clicker.getName(), curr);
                return true;
            } else {
                Long lastClick = cd.get(clicker.getName());
                cd.put(clicker.getName(), curr);
                long diff = curr - lastClick;
                if (diff >= GuiManager.cooldown) {
                    return true;
                }

                YcoordCore.getInstance().getChatMessage().sendMessageId(MessageBase.Level.ERROR, clicker, "messages.cooldown", new MessagePlaceholders(clicker));

            }
        }
        return false;
    }

    private void handleClick(boolean left, Player player, MessagePlaceholders placeholders) {
        ConfigurationSection clickSection;
        clickSection = section.getConfigurationSection("click");
        if (clickSection == null) {
            if (left)
                clickSection = section.getConfigurationSection("left-click");
            else
                clickSection = section.getConfigurationSection("right-click");
            if (clickSection == null)
                return;
        }
        ClickHandler clickHandler = new ClickHandler(left, player, clickSection, placeholders);
        clickHandler.handle();
    }

    public boolean handleClick(GuiBase gui, InventoryClickEvent event, MessagePlaceholders placeholders) {
        Inventory inventory = gui.getInventory();
        ItemStack itemStack = inventory.getItem(event.getSlot());
        if (itemStack == null || itemStack.getType() == Material.AIR)
            return false;
        if (event.getWhoClicked() instanceof Player clicker) {
            if (!checkCooldown(clicker))
                return false;
            getExtraPlaceholders(placeholders, event.getSlot(), gui);
            if (event.isLeftClick()) {
                handleClick(true, clicker, placeholders);
            }

            if (event.isRightClick()) {
                handleClick(false, clicker, placeholders);
            }
        }

        return true;
    }

    public void update(GuiBase guiBase, int slot, long elapsed, Player player, MessagePlaceholders placeholders) {
        handleCondition(guiBase, slot, player, placeholders);
    }

    private void handleCondition(GuiBase guiBase, int slot, Player player, MessagePlaceholders messagePlaceholders) {
        boolean condition = checkCondition(player);

        HashMap<Integer, GuiItem> slots = guiBase.getSlots();

        if (!slots.containsKey(slot)) {
            return;
        }
        if (!condition)
            return;
        GuiItem itemInSlot = slots.get(slot);

        boolean otherCondition = itemInSlot.checkCondition(player);
        if (!otherCondition) {
            guiBase.setSlotItem(slot, this, player, messagePlaceholders);
        } else {
            if (priority > itemInSlot.priority || itemInSlot == this) {
                guiBase.setSlotItem(slot, this, player, messagePlaceholders);
            }
        }
    }

    private boolean checkCondition(Player player) {
        String conditionValue = section.getString("condition", null);
        if (conditionValue == null) {
            return true;
        }

        return Utils.checkCondition(player, conditionValue, new MessagePlaceholders(player));
    }

    protected void getExtraPlaceholders(MessagePlaceholders placeholders, int slot, GuiBase base) {
        placeholders.put("%slot%", slot);
    }

    public boolean isRedraw() {
        return redraw;
    }

    static class ClickHandler {
        private final boolean left;
        private final Player player;
        private final ConfigurationSection section;
        private final MessagePlaceholders placeholders;

        public ClickHandler(boolean left, Player player, ConfigurationSection section, MessagePlaceholders placeholders) {
            this.left = left;
            this.player = player;
            this.section = section;
            this.placeholders = placeholders;
        }

        private ParsedTag parseTaggedLine(String line) {
            // Убираем лишние пробелы и нормализуем
            line = line.trim().replaceAll("\\s+", " ");

            // Поддерживает: [tag] + пробелы + значение
            Pattern pattern = Pattern.compile("^\\[(\\w+)]\\s*(.*)$");
            Matcher matcher = pattern.matcher(line);

            if (matcher.matches()) {
                String tag = matcher.group(1);     // напр. "console"
                String value = matcher.group(2);   // напр. "feed %player%"
                return new ParsedTag(tag, value);
            }

            return null; // или выбросить исключение
        }

        private boolean checkPermission() {
            String permission = section.getString("permission", null);
            if (permission == null) {
                return true;
            }
            if (player.hasPermission(permission)) {
                return true;
            }

            String noPermissionMessageId = section.getString("no-permission-message-id");
            YcoordCore.getInstance().getChatMessage().sendMessageId(MessageBase.Level.ERROR, player, noPermissionMessageId, new MessagePlaceholders(player));
            return false;
        }

        private boolean checkCondition() {
            String condition = section.getString("condition", null);
            if (condition == null) {
                return true;
            }

            if (Utils.checkCondition(player, condition, new MessagePlaceholders(player)))
                return true;

            String noCheckMessageId = section.getString("no-condition-message-id");
            YcoordCore.getInstance().getChatMessage().sendMessageId(MessageBase.Level.ERROR, player, noCheckMessageId, new MessagePlaceholders(player));
            return false;
        }

        public void handle() {
            ConfigurationSection clickSettings = this.section;
            if (clickSettings == null)
                return;
            if (!checkPermission())
                return;
            if (!checkCondition())
                return;
            List<String> actions = section.getStringList("actions");
            ChatMessage chatMessage = YcoordCore.getInstance().getChatMessage();
            for (String action : actions) {
                ParsedTag tag = parseTaggedLine(action);
                if (tag == null)
                    continue;
                if (tag.tag.equalsIgnoreCase("message")) {
                    String message = MessageBase.makeMessage(MessageBase.Level.NONE, tag.value, this.placeholders);
                    player.sendMessage(message);
                } else if (tag.tag.equalsIgnoreCase("id-none")) {
                    String message = tag.value;
                    chatMessage.sendMessageId(MessageBase.Level.NONE, player, message, placeholders);
                } else if (tag.tag.equalsIgnoreCase("id-info")) {
                    String message = tag.value;
                    chatMessage.sendMessageId(MessageBase.Level.INFO, player, message, placeholders);
                } else if (tag.tag.equalsIgnoreCase("id-error")) {
                    String message = tag.value;
                    chatMessage.sendMessageId(MessageBase.Level.ERROR, player, message, placeholders);
                } else if (tag.tag.equalsIgnoreCase("id-success")) {
                    String message = tag.value;
                    chatMessage.sendMessageId(MessageBase.Level.SUCCESS, player, message, placeholders);
                } else if (tag.tag.equalsIgnoreCase("console")) {
                    Utils.executeConsole(player, MessageBase.makeMessage(MessageBase.Level.NONE, tag.value, this.placeholders));
                } else if (tag.tag.equalsIgnoreCase("player")) {
                    Utils.executePlayer(player, MessageBase.makeMessage(MessageBase.Level.NONE, tag.value, this.placeholders));
                }
            }
        }

        protected static class ParsedTag {
            public final String tag;
            public final String value;

            public ParsedTag(String tag, String value) {
                this.tag = tag;
                this.value = value;
            }
        }
    }
}
