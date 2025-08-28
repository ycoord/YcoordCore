package ru.ycoord.core.gui;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import ru.ycoord.core.gui.items.GuiBackButton;
import ru.ycoord.core.gui.items.GuiItem;
import ru.ycoord.core.gui.items.GuiPaginationButton;
import ru.ycoord.core.messages.MessagePlaceholders;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

abstract public class GuiPaged extends GuiBase {
    protected int current = 0;
    protected int markerCount = 0;
    protected int markerWidth = 0;
    protected int markerHeight = 0;

    public GuiPaged(ConfigurationSection section) {
        super(section);

        List<String> pattern = section.getStringList("pattern");
        ConfigurationSection items = section.getConfigurationSection("items");

        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (int i = 0; i < pattern.size(); i++) {
            for (int j = 0; j < pattern.get(i).length(); j++) {
                char c = pattern.get(i).charAt(j);
                String stringC = String.valueOf(c);

                assert items != null;

                Set<String> keys = items.getKeys(false);
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
                    if (type.equalsIgnoreCase("MARKER"))
                        markerCount++;

                    if (i >= maxY)
                        maxY = i;
                    if (i <= minY)
                        minY = i;

                    if (j >= maxX)
                        maxX = j;
                    if (j <= minX)
                        minX = j;
                }
            }
        }
        markerWidth = maxX - minX + 1;
        markerHeight = maxY - minY;
    }

    public int getCurrentPageIndex() {
        return current;
    }

    protected abstract GuiItem getItem(int dataIndex, int currentMarkerIndex, int priority, OfflinePlayer player, int slotIndex, String type, ConfigurationSection config);

    protected GuiItem getFillerItem(int priority, int currentMarkerIndex, OfflinePlayer player, int slotIndex) {
        ConfigurationSection section = this.section.getConfigurationSection("filler-item");
        if (section == null)
            return null;
        return new GuiItem(priority, slotIndex, currentMarkerIndex, section);
    }

    @Override
    public void getExtraPlaceholders(MessagePlaceholders placeholders) {
        super.getExtraPlaceholders(placeholders);
        placeholders.put("%page%", current + 1);
        placeholders.put("%pages%", getMaxPages(placeholders.getPlayer()));
    }

    protected void prepareData(OfflinePlayer player, ConfigurationSection section){

    }

    @Override
    protected ConcurrentHashMap<Integer, List<GuiItemCharacter>> make(OfflinePlayer player, ConfigurationSection section) {
        prepareData(player, section);
        return super.make(player, section);
    }

    protected abstract int getItemCount(OfflinePlayer player);

    @Override
    public GuiItem makeItem(int currentIndex, int slot, int priority, OfflinePlayer player, String type, ConfigurationSection section) {
        if (type.equalsIgnoreCase("MARKER")) {
            int itemCount = getItemCount(player);
            int dataIndex = calculateDataIndex(currentIndex);
            if (dataIndex >= itemCount) {
                return getFillerItem(priority, currentIndex, player, slot);
            }
            return getItem(dataIndex, currentIndex, priority, player, slot, type, section);
        } else if (type.equalsIgnoreCase("PREV")) {
            return new GuiPaginationButton(priority, this, false, slot, currentIndex, section);
        } else if (type.equalsIgnoreCase("NEXT")) {
            return new GuiPaginationButton(priority, this, true, slot, currentIndex, section);
        } else if (type.equalsIgnoreCase("BACK")) {
            return new GuiBackButton(this, priority, slot, currentIndex, section);
        }

        return super.makeItem(currentIndex, slot, priority, player, type, section);
    }

    abstract protected int calculateDataIndex(int currentIndex);

    public void next(Player player, boolean animate) {
        if (current < getMaxPages(player) - 1) {
            current++;
            rebuild(player, animate);
        }
    }


    abstract public int getMaxPages(OfflinePlayer player);


    public void prev(Player player, boolean animate) {
        if (current >= 1) {
            current--;
            rebuild(player, animate);
        }
    }
}
