package ru.ycoord.core.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PlaceholderManager extends PlaceholderExpansion {
    private final HashMap<String, IPlaceholderAPI> papis = new HashMap<>();

    @Override
    public @NotNull String getIdentifier() {
        return "ycoord";
    }

    @Override
    public @NotNull String getAuthor() {
        return "ycoord";
    }

    @Override
    public @NotNull String getVersion() {
        return "";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        String[] args = identifier.split("_");
        if (args.length >= 1) {
            String registered = args[0];
            if (!papis.containsKey(registered))
                return null;
            List<String> l = Arrays.stream(args).toList();
            return papis.get(registered).process(player, l.subList(1, l.size()));
        }

        return null;
    }


    public void registerPlaceholder(IPlaceholderAPI papi) {
        papis.put(papi.getId(), papi);
    }
}
