package ru.ycoord.examples.guis;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import ru.ycoord.core.gui.GuiListData;
import ru.ycoord.core.gui.GuiPagedData;
import ru.ycoord.core.gui.GuiBase;
import ru.ycoord.core.gui.items.GuiItem;
import ru.ycoord.core.messages.MessagePlaceholders;

public class ExampleGuiPagedData extends GuiListData {
    public ExampleGuiPagedData(ConfigurationSection section) {
        super(section);
    }

    static class Marker extends GuiItem {

        private final int dataIndex;

        public Marker(int dataIndex, int priority, int slot, int index, @Nullable ConfigurationSection section) {
            super(priority, slot, index, section);
            this.dataIndex = dataIndex;
        }

        @Override
        protected void getExtraPlaceholders(MessagePlaceholders placeholders, int slot, int index, GuiBase base) {
            super.getExtraPlaceholders(placeholders, slot, index, base);
            placeholders.put("%data-index%", dataIndex);
        }
    }

    @Override
    protected GuiItem getItem(int dataIndex, int currentMarkerIndex, int priority, OfflinePlayer player, int slotIndex, String type, ConfigurationSection config) {
        return new Marker(dataIndex, priority, slotIndex, currentMarkerIndex, config);
    }

    @Override
    protected int getItemCount(OfflinePlayer player) {
        return 53;
    }
}
