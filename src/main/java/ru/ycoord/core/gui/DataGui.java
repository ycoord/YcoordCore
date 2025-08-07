package ru.ycoord.core.gui;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.ycoord.core.gui.items.GuiItem;
import ru.ycoord.core.gui.items.GuiItemStack;
import ru.ycoord.core.messages.MessagePlaceholders;

import java.util.HashMap;
import java.util.List;

public abstract class DataGui extends GuiBase {

    public DataGui(ConfigurationSection section) {
        super(section);
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
            if (currentIndex >= itemCount) {
                return getFillerItem(priority, currentIndex, player, slot);
            }
            return getItem(currentIndex + currentPage * itemCount, currentIndex, priority, player, slot, type, section);
        }
        return super.makeItem(currentIndex, slot, priority, player, type, section);
    }

    protected GuiItem getFillerItem(int priority, int currentMarkerIndex, OfflinePlayer player, int slotIndex) {
        return new GuiItemStack(new ItemStack(Material.BARRIER), priority, slotIndex, currentMarkerIndex, null);
    }

    protected abstract int getItemCount(OfflinePlayer player);

    public int getMaxPages(OfflinePlayer player) {
        if (!typeCounter.containsKey("MARKER"))
            return 0;
        int result = (int) Math.ceil((double) getItemCount(player) / typeCounter.get("MARKER"));
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
