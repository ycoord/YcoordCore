package ru.ycoord.core.messages;

import org.bukkit.OfflinePlayer;

import java.util.Map;

public class MapMessages extends MessagePlaceholders {
    public MapMessages(OfflinePlayer player, Map<String, String> map) {
        super(player);
        this.map = map;
    }
}
