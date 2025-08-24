package ru.ycoord.core.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.ycoord.GradientExtension;
import ru.ycoord.core.placeholder.extensions.InnerExtension;

import java.util.HashMap;

public class PlaceholderManager extends PlaceholderExpansion {
    private final HashMap<String, IPlaceholderAPI> papis = new HashMap<>();

    @Override
    public @NotNull String getIdentifier() {
        return "yc";
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
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String identifier) {
        String[] args = identifier.split("_");
        if (args.length >= 1) {
            String registered = args[0];
            if (!papis.containsKey(registered))
                return null;


            return papis.get(registered).processString(player, identifier);
        }

        return null;
    }

    public PlaceholderManager() {
        registerPlaceholder(new GradientExtension());
        registerPlaceholder(new InnerExtension());
    }


    public void registerPlaceholder(IPlaceholderAPI papi) {
        papis.put(papi.getId(), papi);
    }
}
