package ru.ycoord.examples.commands;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import ru.ycoord.core.gui.GuiBase;
import ru.ycoord.core.gui.items.GuiHeadItem;
import ru.ycoord.core.gui.items.GuiItem;
import ru.ycoord.core.messages.MessagePlaceholders;

public class GuiExample extends GuiBase {
    public GuiExample(ConfigurationSection section) {
        super(section);
    }

    class HelloWorldButton extends GuiItem{

        public HelloWorldButton(int priority, ConfigurationSection section) {
            super(priority, section);
        }

        @Override
        public boolean handleClick(GuiBase gui, InventoryClickEvent event, MessagePlaceholders placeholders) {
            if(!super.handleClick(gui, event, placeholders)){
                return false;
            }

            if(event.getWhoClicked() instanceof Player player){
                player.sendMessage("hello world from code!");
            }

            return true;
        }
    }

    @Override
    public GuiItem makeItem(int priority, OfflinePlayer player, String type, ConfigurationSection section) {
        if(type.equalsIgnoreCase("HELLO_WORLD")){
            return new  HelloWorldButton(priority, section);
        }
        return super.makeItem(priority, player, type, section);
    }
}
