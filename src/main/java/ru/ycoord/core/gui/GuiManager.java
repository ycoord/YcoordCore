package ru.ycoord.core.gui;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;
import ru.ycoord.core.gui.items.GuiBackButton;
import ru.ycoord.core.gui.items.GuiItem;
import ru.ycoord.core.gui.items.GuiPaginationButton;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

public class GuiManager implements Listener {
    public ConcurrentHashMap<Player, Stack<GuiBase>> guis = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ConfigurationSection> guiElements = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, Long> cooldowns = new ConcurrentHashMap<>();
    public static int cooldown = 100;

    public ConcurrentHashMap<String, ConfigurationSection> getGlobalElements() {
        return guiElements;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null)
            return;
        if (event.getClickedInventory().getHolder() == null)
            return;

        InventoryHolder inventoryHolder = event.getClickedInventory().getHolder();
        if (inventoryHolder instanceof GuiBase holder) {
            holder.handleClick((Player) event.getWhoClicked(), event);
        } else if (event.getInventory().getHolder() instanceof GuiBase holder) {
            holder.handleClickInventory((Player) event.getWhoClicked(), event);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() == null)
            return;

        int topSize = event.getView().getTopInventory().getSize();
        Set<Integer> raw = event.getRawSlots();
        boolean touchesTop = raw.stream().anyMatch(s -> s < topSize);
        if (touchesTop) {
            InventoryHolder inventoryHolder = event.getInventory().getHolder();
            if (inventoryHolder instanceof GuiBase holder) {
                holder.handleDrag((Player) event.getWhoClicked(), event);
            }
        }
    }


    public void addGlobalItem(ConfigurationSection section) {
        String symbol = section.getString("symbol", null);
        if (symbol == null)
            return;

        guiElements.put(symbol, section);
    }


    public GuiItem getGlobalItem(GuiBase gui, ConfigurationSection globalItemSection, int slot, int index) {

        String type = globalItemSection.getString("type", "ITEM");

        if (type.equalsIgnoreCase("ITEM")) {
            return new GuiItem(0, slot, index, globalItemSection);
        } else if (type.equalsIgnoreCase("NEXT")) {
            if (gui instanceof GuiPaged paged) {
                return new GuiPaginationButton(0, paged, true, slot, index, globalItemSection);
            }
        } else if (type.equalsIgnoreCase("PREV")) {
            if (gui instanceof GuiPaged paged) {
                return new GuiPaginationButton(0, paged, false, slot, index, globalItemSection);
            }
        } else if (type.equalsIgnoreCase("BACK")) {
            if (gui instanceof GuiPaged paged) {
                return new GuiBackButton(paged, 0, slot, index, globalItemSection);
            }
        }
        return null;
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
