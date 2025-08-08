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

public class GuiSlot extends GuiItem {
    protected final IItemProvider provider;

    public interface IItemProvider {
        void saveAll(Player clicker, Inventory inventory);

        ItemStack getItem();
    }

    public GuiSlot(IItemProvider provider, int priority, int slot, int index, @Nullable ConfigurationSection section) {
        super(priority, slot, index, section);
        this.provider = provider;
    }

    @Override
    public ItemStack buildItem(OfflinePlayer clicker, GuiBase base, int slot, int index, MessagePlaceholders placeholders, boolean onlyMeta) {
        return provider.getItem();
    }

    @Override
    public void update(GuiBase guiBase, int slot, int index, long elapsed, Player player, MessagePlaceholders placeholders) {

    }


    @Override
    public boolean handleClick(GuiBase gui, InventoryClickEvent event, MessagePlaceholders placeholders) {
        if (!super.handleClick(gui, event, placeholders))
            return false;
        event.setCancelled(false);
        if (event.getWhoClicked() instanceof Player player) {
            provider.saveAll(player, event.getInventory());
        }
        return true;
    }
}
