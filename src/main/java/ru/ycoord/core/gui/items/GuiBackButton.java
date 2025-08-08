package ru.ycoord.core.gui.items;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.Nullable;
import ru.ycoord.YcoordCore;
import ru.ycoord.core.gui.GuiBase;
import ru.ycoord.core.messages.MessagePlaceholders;

public class GuiBackButton extends GuiItem{
    public GuiBackButton(int priority, int slot, int index, @Nullable ConfigurationSection section) {
        super(priority, slot, index, section);
    }

    @Override
    public boolean handleClick(GuiBase gui, InventoryClickEvent event, MessagePlaceholders placeholders) {
        if(!super.handleClick(gui, event, placeholders))
            return false;
        if(event.getWhoClicked() instanceof Player player){
            YcoordCore.getInstance().getGuiManager().back(player);

        }
        return true;
    }
}
