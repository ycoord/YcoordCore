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


    public String apply(MessageBase.Level level, String text) {
        MessageBase.Style style = YcoordCore.getInstance().getChatMessage().getStyle();
        if (style != null) {
            text = style.apply(level, text);
        }

        Map<String, String> keyValues = new HashMap<>();
        for (String key : map.keySet())
            keyValues.put(key, map.get(key));

        for (String key : YcoordCore.getInstance().getGlobalPlaceholders().keySet())
            keyValues.put(key, YcoordCore.getInstance().getGlobalPlaceholders().get(key));

        String time = convertTime(System.currentTimeMillis());
        keyValues.put("%current_time%", time);
        keyValues.put("%current-time%", time);

        if (player != null) {
            String name = player.getName();

            if (name != null)
                keyValues.put("%executor%", name);
        }

        for (String key : keyValues.keySet()) {
            if (style != null)
                text = style.preparePlaceholder(text, level, key);
        }

        for (String key : keyValues.keySet()) {

            text = text.replace(key, keyValues.get(key));
        }


        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public void add(MessagePlaceholders globalPlaceholders) {
        for (String key : globalPlaceholders.keySet())
            put(key, globalPlaceholders.get(key));
    }
}
