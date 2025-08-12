package ru.ycoord.core.placeholder;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;

import java.util.List;

public class PlaceholderDummy extends IPlaceholderAPI {
    @Override
    public String getId() {
        return "dummy";
    }

    @Override
    public String process(Player player, List<String> args) {
        return "dummy";
    }
}
