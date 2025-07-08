package ru.ycoord.placeholder;

import org.bukkit.entity.Player;

import java.util.List;

public interface IPlaceholderAPI {
    String getId();
    String process(Player player, List<String> args);
}
