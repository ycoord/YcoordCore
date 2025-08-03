package ru.ycoord.examples.commands;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import ru.ycoord.core.gui.GuiBase;
import ru.ycoord.core.gui.items.GuiHeadItem;
import ru.ycoord.core.gui.items.GuiItem;

public class GuiExample extends GuiBase {
    public GuiExample(ConfigurationSection section) {
        super(section);
    }

    @Override
    public GuiItem makeItem(int priority, OfflinePlayer player, String type, ConfigurationSection section) {

        return super.makeItem(priority, player, type, section);
    }
}
