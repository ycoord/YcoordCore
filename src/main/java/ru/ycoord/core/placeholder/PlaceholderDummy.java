package ru.ycoord.core.placeholder;

import org.bukkit.entity.Player;

import java.util.List;

public class PlaceholderDummy implements IPlaceholderAPI {
    @Override
    public String getId() {
        return "dummy";
    }

    @Override
    public String process(Player player, List<String> args) {
        return "dummy";
    }
}
