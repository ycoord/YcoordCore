package ru.ycoord.core.gui.items;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import ru.ycoord.YcoordCore;
import ru.ycoord.core.gui.GuiBase;
import ru.ycoord.core.gui.GuiPaged;
import ru.ycoord.core.messages.MessagePlaceholders;

public class GuiBackButton extends GuiPagedItem {
    public GuiBackButton(GuiPaged paged, int priority, int slot, int index, @Nullable ConfigurationSection section) {
        super(paged, priority, slot, index, section);
    }

    @Override
    public void apply(OfflinePlayer clicker, ItemStack stack, MessagePlaceholders placeholders) {
        super.apply(clicker, stack, placeholders);

        assert section != null;
        if (section.getBoolean("counter", true))
            stack.setAmount(gui.getCurrentPageIndex() + 1);
    }

    @Override
    public boolean handleClick(GuiBase gui, InventoryClickEvent event, MessagePlaceholders placeholders) {
        if (!super.handleClick(gui, event, placeholders))
            return false;
        if (event.getWhoClicked() instanceof Player player) {
            YcoordCore.getInstance().getGuiManager().back(player);

        }
        return true;
    }
}
