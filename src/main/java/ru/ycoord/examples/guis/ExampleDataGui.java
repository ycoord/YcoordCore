package ru.ycoord.examples.guis;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import ru.ycoord.core.gui.DataGui;
import ru.ycoord.core.gui.items.GuiItem;

public class ExampleDataGui extends DataGui {
    public ExampleDataGui(ConfigurationSection section) {
        super(section);
    }

    @Override
    protected GuiItem getItem(int dataIndex, int currentMarkerIndex, int priority, OfflinePlayer player, int slotIndex, String type, ConfigurationSection config) {
        return new GuiItem(priority, slotIndex, currentMarkerIndex, config);
    }

    @Override
    protected int getItemCount(OfflinePlayer player) {
        return 40;
    }
}
