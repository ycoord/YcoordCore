package ru.ycoord.core.gui.items;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ru.ycoord.YcoordCore;
import ru.ycoord.core.gui.GuiBase;
import ru.ycoord.core.messages.MessagePlaceholders;
import ru.ycoord.core.utils.Utils;

public class GuiHeadItem extends GuiItem {
    @NotNull
    private final String name;

    public GuiHeadItem(@NotNull String name, ConfigurationSection section) {
        super(section);
        this.name = name;
    }

    @Override
    public ItemStack buildItem(OfflinePlayer clicker, GuiBase base, int slot, MessagePlaceholders placeholders) {
        ItemStack stack = new ItemStack(Material.PLAYER_HEAD);


        Utils.createPlayerHeadAsync(name).thenAccept(head -> {
            Bukkit.getScheduler().runTask(YcoordCore.getInstance(), () -> {
                apply(clicker, head, placeholders);
                base.getInventory().setItem(slot, head);
            });
        });

        apply(clicker, stack, placeholders);

        return stack;
    }
}
