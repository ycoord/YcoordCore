package ru.ycoord.core.gui.items;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import ru.ycoord.core.gui.GuiBase;
import ru.ycoord.core.gui.GuiPaged;
import ru.ycoord.core.messages.MessagePlaceholders;

public abstract class GuiPagedItem extends GuiItem {
    protected GuiPaged gui;
    public GuiPagedItem(GuiPaged paged, int priority, int slot, int index, @Nullable ConfigurationSection section) {
        super(priority, slot, index, section);
        gui = paged;
    }
}
