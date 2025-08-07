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

public abstract class GuiPagedData extends GuiPaged {
    public GuiPagedData(ConfigurationSection section) {
        super(section);
    }


    @Override
    public void getExtraPlaceholders(MessagePlaceholders placeholders) {
        super.getExtraPlaceholders(placeholders);
        placeholders.put("%page%", current + 1);
        placeholders.put("%pages%", getMaxPages(placeholders.getPlayer()));
    }


    @Override
    protected int calculateDataIndex(int currentIndex) {
        return markerCount * current + currentIndex;
    }

    @Override
    public int getMaxPages(OfflinePlayer player) {
        int result = (int) Math.ceil((double) getItemCount(player) / markerCount);
        if (result == 0)
            return 1;
        return result;
    }
}
