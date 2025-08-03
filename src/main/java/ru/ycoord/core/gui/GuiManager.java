package ru.ycoord.core.gui;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;
import ru.ycoord.core.gui.items.GuiItem;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class GuiManager implements Listener {
    public HashMap<Player, Stack<GuiBase>> guis = new HashMap<>();
    private final HashMap<String, ConfigurationSection> guiElements = new HashMap<>();
    public static HashMap<String, Long> cooldowns = new HashMap<>();
    public static int cooldown = 100;
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null)
            return;
        if (event.getClickedInventory().getHolder() == null)
            return;

        InventoryHolder inventoryHolder = event.getClickedInventory().getHolder();
        if (inventoryHolder instanceof GuiBase holder) {
            event.setCancelled(true);

            holder.handleClick((Player) event.getWhoClicked(), event);

        } else if (event.getInventory().getHolder() instanceof GuiBase holder) {
            holder.handleClickInventory((Player) event.getWhoClicked(), event);
            if (event.isCancelled())
                return;
            event.setCancelled(true);
        }
    }

    public void addGlobalItem(ConfigurationSection section) {
        String symbol = section.getString("symbol", null);
        if (symbol == null)
            return;

        guiElements.put(symbol, section);
    }

    public GuiItem getGlobalItem(String symbol) {
        if (!guiElements.containsKey(symbol))
            return null;
        return new GuiItem(guiElements.get(symbol));
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onEvent(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        if (!guis.containsKey(player))
            guis.put(player, new Stack<>());
        List<GuiBase> g = guis.get(player);

        if (event.getInventory().getHolder() == null)
            return;

        if (event.getInventory().getHolder() instanceof GuiBase hol) {
            if (!g.contains(hol))
                g.add(hol);
        }

    }

    public void back(Player player) {
        if (!guis.containsKey(player))
            return;
        if (guis.get(player).empty()) {
            player.closeInventory();
            return;
        }
        guis.get(player).pop();
        if (guis.get(player).empty()) {
            player.closeInventory();
            return;
        }
        guis.get(player).peek().open(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEvent(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() == null)
            return;

        if (event.getInventory().getHolder() instanceof GuiBase base) {
            base.onClose(event);
            if (event.getReason() == InventoryCloseEvent.Reason.OPEN_NEW) {

                return;
            }
        }
        if (event.getPlayer() instanceof Player player)
            guis.remove(player);
    }

    public void onDisable() {
        guis.clear();
        cooldowns.clear();
    }

    public void update(long elapsed) {
        for (Player player : guis.keySet()) {
            Stack<GuiBase> stack = guis.get(player);
            if (stack.empty())
                continue;
            GuiBase base = stack.peek();
            base.update(elapsed, player);
        }
    }
}
