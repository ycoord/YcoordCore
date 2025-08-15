package ru.ycoord.examples.guis;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import ru.ycoord.core.gui.GuiBase;
import ru.ycoord.core.gui.GuiListData;
import ru.ycoord.core.gui.items.GuiItem;
import ru.ycoord.core.gui.items.GuiItemStack;
import ru.ycoord.core.messages.MessagePlaceholders;

public class ExampleGuiListData extends GuiListData {
    public ExampleGuiListData(ConfigurationSection section) {
        super(section);
    }

    static class Marker extends GuiItemStack {

        private final int dataIndex;

        public Marker(ItemStack stack, int dataIndex, int priority, int slot, int index, @Nullable ConfigurationSection section) {
            super(stack, priority, slot, index, section);
            this.dataIndex = dataIndex;
        }

        @Override
        public void getExtraPlaceholders(OfflinePlayer player, MessagePlaceholders placeholders, int slot, int index, GuiBase base) {
            super.getExtraPlaceholders(player, placeholders, slot, index, base);
            placeholders.put("%data-index%", dataIndex);
        }


    }

    @Override
    protected GuiItem getItem(int dataIndex, int currentMarkerIndex, int priority, OfflinePlayer player, int slotIndex, String type, ConfigurationSection config) {
        return new Marker(new ItemStack(Material.values()[20 + dataIndex]), dataIndex, priority, slotIndex, currentMarkerIndex, config);
    }

    @Override
    protected int getItemCount(OfflinePlayer player) {
        return 53;
    }
}
