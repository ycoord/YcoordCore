package ru.ycoord.core.messages;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import ru.ycoord.YcoordCore;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static ru.ycoord.core.utils.Utils.convertTime;

public class MessagePlaceholders {
    private OfflinePlayer player;

    public MessagePlaceholders(OfflinePlayer player) {
        this.player = player;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public void setPlayer(OfflinePlayer player) {
        this.player = player;
    }

    protected Map<String, String> map = new HashMap<>();

    public Set<String> keySet() {
        return map.keySet();
    }

    public void put(String key, Object value) {
        map.put(key, String.valueOf(value));
    }

    public String get(String key) {
        return map.get(key);
    }

    public String apply(String text) {
        for (String key : map.keySet())
            text = text.replace(key, map.get(key));


        for (String key : YcoordCore.getInstance().getGlobalPlaceholders().keySet())
            text = text.replace(key, YcoordCore.getInstance().getGlobalPlaceholders().get(key));

        text = text.replace("%current_time%", convertTime(System.currentTimeMillis()));

        if (player != null) {
            String name = player.getName();

            if (name != null)
                text = text.replace("%executor%", name);
        }

        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public void add(MessagePlaceholders globalPlaceholders) {
        for (String key : globalPlaceholders.keySet())
            put(key, globalPlaceholders.get(key));
    }
}
