package ru.ycoord.core.gui.items;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import ru.ycoord.core.gui.GuiPaged;
import ru.ycoord.core.gui.GuiPagedData;
import ru.ycoord.core.gui.GuiBase;
import ru.ycoord.core.messages.MessagePlaceholders;

public class GuiPaginationButton extends GuiItem {
    private final GuiPaged parent;
    private final boolean forward;

    public GuiPaginationButton(int priority, GuiPaged parent, boolean forward, int slot, int index, ConfigurationSection section) {
        super(priority, slot, index, section);
        this.parent = parent;
        this.forward = forward;
    }

    @Override
    public boolean handleClick(GuiBase gui, InventoryClickEvent event, MessagePlaceholders placeholders) {
        boolean result = super.handleClick(gui, event, placeholders);
        if(!result)
            return false;

        if(event.getWhoClicked() instanceof Player player){
            if(forward)
                parent.next(player, true);
            else
                parent.prev(player, true);
        }

        return true;
    }
}
