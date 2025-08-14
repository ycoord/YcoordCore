package ru.ycoord.core.gui;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import ru.ycoord.core.messages.MessagePlaceholders;

public abstract class GuiPagedData extends GuiPaged {
    public GuiPagedData(ConfigurationSection section) {
        super(section);
    }


    @Override
    public void getExtraPlaceholders(MessagePlaceholders placeholders) {
        super.getExtraPlaceholders(placeholders);
        placeholders.put("%page%", current + 1);
        placeholders.put("%page-index%", current);
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
