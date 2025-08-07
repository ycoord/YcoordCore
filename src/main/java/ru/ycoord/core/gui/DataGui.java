package ru.ycoord.core.gui;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.ycoord.core.gui.items.GuiItem;
import ru.ycoord.core.gui.items.GuiItemStack;
import ru.ycoord.core.gui.items.GuiPaginationButton;
import ru.ycoord.core.messages.MessagePlaceholders;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public abstract class DataGui extends GuiBase {

    private int markerCount = 0;
    public DataGui(ConfigurationSection section) {
        super(section);

        List<String> pattern = section.getStringList("pattern");
        ConfigurationSection items = section.getConfigurationSection("items");


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
                }
            }
        }
    }

    private int currentPage = 0;

    @Override
    protected HashMap<Integer, List<GuiItemCharacter>> make(OfflinePlayer player, ConfigurationSection section) {
        return super.make(player, section);
    }

    protected abstract GuiItem getItem(int dataIndex, int currentMarkerIndex, int priority, OfflinePlayer player, int slotIndex, String type, ConfigurationSection config);

    @Override
    public void getExtraPlaceholders(MessagePlaceholders placeholders) {
        super.getExtraPlaceholders(placeholders);
        placeholders.put("%page%", currentPage + 1);
        placeholders.put("%pages%", getMaxPages(placeholders.getPlayer()));
    }

    @Override
    public GuiItem makeItem(int currentIndex, int slot, int priority, OfflinePlayer player, String type, ConfigurationSection section) {
        if (type.equalsIgnoreCase("MARKER")) {
            int itemCount = getItemCount(player);
            int dataIndex = currentIndex + currentPage * markerCount;
            if (dataIndex >= itemCount) {
                return getFillerItem(priority, currentIndex, player, slot);
            }
            return getItem(dataIndex, currentIndex, priority, player, slot, type, section);
        } else if (type.equalsIgnoreCase("NEXT")) {
            return new GuiPaginationButton(priority, this, true, slot, currentIndex, section);
        } else if (type.equalsIgnoreCase("PREV")) {
            return new GuiPaginationButton(priority, this, false, slot, currentIndex, section);
        }
        return super.makeItem(currentIndex, slot, priority, player, type, section);
    }

    protected GuiItem getFillerItem(int priority, int currentMarkerIndex, OfflinePlayer player, int slotIndex) {
        ConfigurationSection section = this.section.getConfigurationSection("filler-item");
        if (section == null)
            return null;
        return new GuiItem(priority, slotIndex, currentMarkerIndex, section);
    }

    protected abstract int getItemCount(OfflinePlayer player);

    public int getMaxPages(OfflinePlayer player) {
        int result = (int) Math.ceil((double) getItemCount(player) / markerCount);
        if (result == 0)
            return 1;
        return result;
    }

    public void next(Player player) {
        if (currentPage < getMaxPages(player) - 1) {
            currentPage++;
            rebuild(player);
        }
    }

    public void prev(Player player) {
        if (currentPage >= 1) {
            currentPage--;
            rebuild(player);
        }
    }
}
