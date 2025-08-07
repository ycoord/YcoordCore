package ru.ycoord.core.gui;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import ru.ycoord.core.gui.items.GuiItem;
import ru.ycoord.core.gui.items.GuiPaginationButton;
import ru.ycoord.core.messages.MessagePlaceholders;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public abstract class GuiListData extends GuiPaged {
    public GuiListData(ConfigurationSection section) {
        super(section);
    }

    protected abstract GuiItem getItem(int dataIndex, int currentMarkerIndex, int priority, OfflinePlayer player, int slotIndex, String type, ConfigurationSection config);


    protected abstract int getItemCount(OfflinePlayer player);

    @Override
    protected int calculateDataIndex(int currentIndex) {
        return markerWidth * current + currentIndex;
    }

    @Override
    public int getMaxPages(OfflinePlayer player) {
        int count = getItemCount(player);
        int cols = (int) Math.ceil((double) count / markerWidth);
        return cols - markerHeight + 1;
    }
}
