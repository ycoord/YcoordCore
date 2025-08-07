package ru.ycoord.core.gui.items;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import ru.ycoord.core.gui.DataGui;
import ru.ycoord.core.gui.GuiBase;
import ru.ycoord.core.messages.MessagePlaceholders;

public class GuiPaginationButton extends GuiItem {
    private final DataGui parent;
    private final boolean forward;

    public GuiPaginationButton(int priority, DataGui parent, boolean forward, int slot, int index, ConfigurationSection section) {
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
                parent.next(player);
            else
                parent.prev(player);
        }

        return true;
    }


    //@Override
    //public boolean handleClick(GuiBase gui, @NotNull Player whoClicked, @NotNull InventoryClickEvent e) {
    //    boolean result = super.handleClick(gui, whoClicked, e);
    //    if (!result)
    //        return false;
    //    if (forward)
    //        parent.next(whoClicked);
    //    else
    //        parent.prev(whoClicked);
//
    //    return true;
    //}
}
