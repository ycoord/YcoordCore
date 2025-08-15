package ru.ycoord.core.gui.items;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.ycoord.core.gui.GuiBase;
import ru.ycoord.core.messages.MessagePlaceholders;
import ru.ycoord.core.transaction.TransactionManager;

public class GuiSlot extends GuiItem {
    protected IItemProvider provider;

    public interface IItemProvider {
        ItemStack getItem(OfflinePlayer player);
    }

    public GuiSlot(IItemProvider provider, int priority, int slot, int index, @Nullable ConfigurationSection section) {
        super(priority, slot, index, section);
        this.provider = provider;
    }

    @Override
    public ItemStack buildItem(OfflinePlayer clicker, GuiBase base, int slot, int index, MessagePlaceholders placeholders, boolean onlyMeta) {
        return provider.getItem(clicker);
    }

    @Override
    public void update(GuiBase guiBase, int slot, int index, long elapsed, Player player, MessagePlaceholders placeholders) {

    }

    @Override
    public boolean handleClick(GuiBase gui, InventoryClickEvent event, MessagePlaceholders placeholders) {
        if(!super.handleClick(gui, event, placeholders))
            return false;

        event.setCancelled(false);

        return true;
    }

    //@Override
    //public boolean handleClick(GuiBase gui, InventoryClickEvent event, MessagePlaceholders placeholders) {
    //    if (!super.handleClick(gui, event, placeholders))
    //        return false;
//
    //    if (event.getWhoClicked() instanceof Player player) {
    //            if(TransactionManager.inProgress(player.getName(), "SAVE_SLOT_DATA"))
    //                return false;
    //
    //            event.setCancelled(false);
    //            provider.saveAll(player, event.getInventory());
    //        }
    //    return true;
    //}


}
