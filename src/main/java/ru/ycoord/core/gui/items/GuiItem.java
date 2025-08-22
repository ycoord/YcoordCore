package ru.ycoord.core.gui.items;


import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import ru.ycoord.YcoordCore;
import ru.ycoord.core.gui.GuiBase;
import ru.ycoord.core.gui.GuiManager;
import ru.ycoord.core.messages.ChatMessage;
import ru.ycoord.core.messages.MessageBase;
import ru.ycoord.core.messages.MessagePlaceholders;
import ru.ycoord.core.sound.SoundInfo;
import ru.ycoord.core.utils.Utils;
import ru.ycoord.examples.commands.GuiExample;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuiItem {
    protected final int slot;
    protected final int index;
    protected @Nullable ConfigurationSection section;
    protected final int priority;
    protected boolean redraw = false;
    protected List<String> lore = new LinkedList<>();
    protected ItemStack stack;

    public GuiItem(int priority, int slot, int index, @Nullable ConfigurationSection section) {
        this.priority = priority;
        this.section = section;
        this.slot = slot;
        this.index = index;
        if (section == null)
            return;


        this.lore = section.getStringList("lore");
        this.redraw = section.getBoolean("redraw", false);
    }

    public List<String> getLoreBefore(OfflinePlayer ignored) {
        return new LinkedList<>();
    }

    public List<String> getLoreAfter(OfflinePlayer ignored) {
        return new LinkedList<>();
    }

    public void apply(OfflinePlayer clicker, ItemStack stack, MessagePlaceholders placeholders) {
        if (section == null)
            return;
        List<String> loreBefore = getLoreBefore(clicker);

        List<String> loreAfter = getLoreAfter(clicker);

        List<String> resultLore = new LinkedList<>();

        if(!lore.isEmpty())
        {
            resultLore.add("");
        }

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
        meta.setDisplayName(MessageBase.translateColor(MessageBase.Level.NONE, name, placeholders));

        //List<Component> components = new LinkedList<>();
        //for (String loreItem : resultLore) {
        //    meta.lore(new LinkedList<>());
        //    components.add(Component.text(MessageBase.translateColor(MessageBase.Level.NONE, loreItem, placeholders)));
        //}
//
        //meta.lore(components);

        meta.setLore(resultLore);

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

    public ItemStack buildItem(OfflinePlayer clicker, GuiBase base, int slot, int index, MessagePlaceholders placeholders, boolean onlyMeta) {
        if (!checkCondition(clicker.getPlayer(), placeholders))
            return null;
        getExtraPlaceholders(clicker, placeholders, slot, index, base);
        if (!onlyMeta) {
            this.stack = createItem(clicker, base, slot);
        }
        apply(clicker, this.stack, placeholders);
        return this.stack;
    }

    public ItemStack createItem(OfflinePlayer clicker, GuiBase base, int slot) {
        ItemStack stack;
        if (section == null)
            return new ItemStack(Material.AIR);
        String texture = section.getString("texture", null);
        if (texture != null && !texture.isEmpty()) {
            ItemStack item = Utils.createPlayerHeadBase64(texture);
            base.setSlotItemReady(slot, this, item);
            stack = new ItemStack(Material.PLAYER_HEAD);

        } else {
            stack = new ItemStack(Material.valueOf(section.getString("material", "BARRIER")));
        }
        return stack;
    }

    public boolean checkCooldown(Player clicker) {
        ConcurrentHashMap<String, Long> cd = GuiManager.cooldowns;
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

                YcoordCore.getInstance().getChatMessage().sendMessageIdAsync(MessageBase.Level.ERROR, clicker, "messages.cooldown", new MessagePlaceholders(clicker));

            }
        }
        return false;
    }

    public boolean handleClick(GuiBase gui, boolean left, Player player, MessagePlaceholders placeholders) {
        if (section == null)
            return true;
        ConfigurationSection clickSection;
        clickSection = section.getConfigurationSection("click");
        if (clickSection == null) {
            if (left)
                clickSection = section.getConfigurationSection("left-click");
            else
                clickSection = section.getConfigurationSection("right-click");
        }
        ClickHandler clickHandler = new ClickHandler(gui, player, clickSection, placeholders);
        return clickHandler.handle(placeholders);
    }

    public boolean handleClick(GuiBase gui, InventoryClickEvent event, MessagePlaceholders placeholders) {
        event.setCancelled(true);
        if (event.getWhoClicked() instanceof Player clicker) {
            if (!checkCooldown(clicker))
                return false;
            getExtraPlaceholders(clicker, placeholders, slot, index, gui);
            if (event.isLeftClick()) {
                return handleClick(gui, true, clicker, placeholders);
            }

            if (event.isRightClick()) {
                return handleClick(gui, false, clicker, placeholders);
            }
        }

        return true;
    }

    public void update(GuiBase guiBase, int slot, int index, long elapsed, Player player, MessagePlaceholders placeholders) {
        getExtraPlaceholders(player, placeholders, slot, index, guiBase);
        handleCondition(guiBase, slot, index, player, placeholders);
    }

    public void handleCondition(GuiBase guiBase, int slot, int index, Player player, MessagePlaceholders messagePlaceholders) {
        boolean condition = checkCondition(player, messagePlaceholders);


        if (!guiBase.hasSlot(slot)) {
            return;
        }
        if (!condition)
            return;
        GuiItem itemInSlot = guiBase.getItemInSlot(player, slot);

        boolean otherCondition = itemInSlot.checkCondition(player, messagePlaceholders);
        if (!otherCondition) {
            guiBase.setSlotItem(slot, index, this, player, messagePlaceholders);
        } else {
            if (priority > itemInSlot.priority || itemInSlot == this) {
                guiBase.setSlotItem(slot, index, this, player, messagePlaceholders);
            }
        }
    }

    public boolean checkCondition(Player player, MessagePlaceholders placeholders) {
        if (section == null)
            return true;
        String conditionValue = section.getString("condition", null);
        if (conditionValue == null) {
            return true;
        }

        return Utils.checkCondition(player, conditionValue, placeholders);
    }

    public void getExtraPlaceholders(OfflinePlayer player, MessagePlaceholders placeholders, int slot, int index, GuiBase base) {
        placeholders.put("%slot%", slot);
        placeholders.put("%index%", index);
    }

    public boolean isRedraw() {
        return redraw;
    }

    static class ClickHandler {
        private final GuiBase gui;
        private final Player player;
        private final ConfigurationSection section;
        private final MessagePlaceholders placeholders;
        private SoundInfo sound = null;

        public ClickHandler(GuiBase gui, Player player, ConfigurationSection section, MessagePlaceholders placeholders) {
            this.gui = gui;
            this.player = player;
            this.section = section;
            this.placeholders = placeholders;


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

        private boolean checkPermission(MessagePlaceholders placeholders) {
            String permission = section.getString("permission", null);
            if (permission == null) {
                return true;
            }
            if (player.hasPermission(permission)) {
                return true;
            }

            String noPermissionMessageId = section.getString("no-permission-message-id");
            YcoordCore.getInstance().getChatMessage().sendMessageIdAsync(MessageBase.Level.ERROR, player, noPermissionMessageId, placeholders);
            return false;
        }

        private boolean checkCondition(MessagePlaceholders placeholders) {
            String condition = section.getString("condition", null);
            if (condition == null) {
                return true;
            }

            if (Utils.checkCondition(player, condition, placeholders))
                return true;

            String noCheckMessageId = section.getString("no-condition-message-id");
            YcoordCore.getInstance().getChatMessage().sendMessageIdAsync(MessageBase.Level.ERROR, player, noCheckMessageId, placeholders);
            return false;
        }

        public boolean handle(MessagePlaceholders placeholders) {
            if (this.section == null) {
                if (sound != null)
                    sound.play(player);
                return true;
            }
            if (!checkPermission(placeholders))
                return false;
            if (!checkCondition(placeholders))
                return false;
            List<String> actions = section.getStringList("actions");
            ChatMessage chatMessage = YcoordCore.getInstance().getChatMessage();
            for (String action : actions) {
                ParsedTag tag = parseTaggedLine(action);
                if (tag == null)
                    continue;
                if (tag.tag.equalsIgnoreCase("message")) {
                    String message = MessageBase.makeMessage(MessageBase.Level.NONE, tag.value, this.placeholders);
                    player.sendMessage(message);
                } else if (tag.tag.equalsIgnoreCase("id")) {
                    String message = tag.value;
                    chatMessage.sendMessageIdAsync(MessageBase.Level.NONE, player, message, placeholders);
                } else if (tag.tag.equalsIgnoreCase("info")) {
                    String message = tag.value;
                    chatMessage.sendMessageIdAsync(MessageBase.Level.INFO, player, message, placeholders);
                } else if (tag.tag.equalsIgnoreCase("error")) {
                    String message = tag.value;
                    chatMessage.sendMessageIdAsync(MessageBase.Level.ERROR, player, message, placeholders);
                } else if (tag.tag.equalsIgnoreCase("success")) {
                    String message = tag.value;
                    chatMessage.sendMessageIdAsync(MessageBase.Level.SUCCESS, player, message, placeholders);
                } else if (tag.tag.equalsIgnoreCase("console")) {
                    Utils.executeConsole(player, MessageBase.makeMessage(MessageBase.Level.NONE, tag.value, this.placeholders));
                } else if (tag.tag.equalsIgnoreCase("player")) {
                    Utils.executePlayer(player, MessageBase.makeMessage(MessageBase.Level.NONE, tag.value, this.placeholders));
                } else if (tag.tag.equalsIgnoreCase("close")) {
                    player.closeInventory();
                } else if (tag.tag.equalsIgnoreCase("update")) {
                    gui.rebuild(player, false);
                } else if (tag.tag.equalsIgnoreCase("animate")) {
                    gui.rebuild(player, true);
                } else if (tag.tag.equalsIgnoreCase("sound")) {
                    try {
                        sound = new SoundInfo(Sound.valueOf(tag.value), SoundCategory.AMBIENT, 0.5f, 0);
                    } catch (Exception ignored) {

                    }
                } else if (tag.tag.equalsIgnoreCase("open")) {
                    ConfigurationSection section = YcoordCore.getInstance().getMenus().get(tag.value);
                    if (section != null) {
                        GuiExample base = new GuiExample(section);
                        base.open(player);
                    }
                }

            }

            if (sound != null)
                sound.play(player);
            return true;
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
