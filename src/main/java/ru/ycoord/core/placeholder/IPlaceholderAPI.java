package ru.ycoord.core.placeholder;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

abstract public class IPlaceholderAPI {
    public abstract String getId();
    public abstract String process(OfflinePlayer player, List<String> args);
    public String processString(OfflinePlayer player, String identifier){
        String[] args = identifier.split("_");
        if (args.length >= 1) {

            List<String> l = Arrays.stream(args).toList();
            return process(player, l.subList(1, l.size()));
        }
        return null;
    }
}
