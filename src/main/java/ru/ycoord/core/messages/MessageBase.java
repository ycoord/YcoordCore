package ru.ycoord.core.messages;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.ycoord.YcoordCore;
import ru.ycoord.core.sound.SoundInfo;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class MessageBase {
    protected ConfigurationSection messagesSection;

    public void addConfig(@Nullable ConfigurationSection config) {
        if (!messagesSection.contains("messages")) {
            messagesSection.createSection("messages");
        }

        ConfigurationSection messages = messagesSection.getConfigurationSection("messages");
        if (config == null)
            return;
        ConfigurationSection otherMessages = config.getConfigurationSection("messages");
        if (otherMessages == null) {
            return;
        }

        for (String key : otherMessages.getKeys(false)) {
            assert messages != null;
            messages.set(key, otherMessages.get(key));
        }
    }

    public static class Style {
        public static class LevelStyle {
            public String prefix;
            public String placeholder;
            public String mainColor;
            public SoundInfo sound = null;

            public LevelStyle(ConfigurationSection section) {
                this.prefix = section.getString("prefix", "");
                this.placeholder = section.getString("placeholder", "");
                this.mainColor = section.getString("color", "");
                ConfigurationSection soundSection = section.getConfigurationSection("sound");
                if (soundSection != null)
                    this.sound = new SoundInfo(soundSection);
            }

            public String apply(String text) {
                if (prefix != null && mainColor != null) {
                    return String.format("%s%s%s", prefix, text, mainColor);
                }
                return text;
            }

            public String preparePlaceholder(String text, String key) {
                StringBuilder sb = new StringBuilder(key);
                sb.insert(1, "!");
                String nonFormat = sb.toString();
                String temp = String.format("%s%s%s", placeholder, key, mainColor);
                key = key.replace("{", "\\{").replace("}", "\\}");
                key = key.replace("%", "\\%");

                nonFormat = nonFormat.replace("{", "\\{").replace("}", "\\}");
                nonFormat = nonFormat.replace("%", "\\%");

                text = text.replaceAll(key, temp);
                text = text.replaceAll(nonFormat, key);

                return text;
            }

            public void playSound(OfflinePlayer listener) {
                if (sound == null)
                    return;
                this.sound.play(listener);
            }
        }

        public LevelStyle info;
        public LevelStyle success;
        public LevelStyle error;

        private Style(ConfigurationSection section) {

            ConfigurationSection info = section.getConfigurationSection("info");
            ConfigurationSection success = section.getConfigurationSection("success");
            ConfigurationSection error = section.getConfigurationSection("error");

            if (info != null) {
                this.info = new LevelStyle(info);
            }

            if (success != null) {
                this.success = new LevelStyle(success);
            }

            if (error != null) {
                this.error = new LevelStyle(error);
            }
        }

        public String apply(Level level, String message) {
            switch (level) {
                case INFO -> {
                    return info.apply(message);
                }
                case SUCCESS -> {
                    return success.apply(message);
                }
                case ERROR -> {
                    return error.apply(message);
                }
                case NONE -> {
                    return message;
                }
            }

            return message;
        }

        public void playSound(Level level, OfflinePlayer listener) {
            switch (level) {
                case INFO -> info.playSound(listener);
                case SUCCESS -> success.playSound(listener);
                case ERROR -> error.playSound(listener);
                case NONE -> {
                }
            }
        }


        public String preparePlaceholder(String text, Level level, String key) {

            switch (level) {
                case INFO -> {
                    return info.preparePlaceholder(text, key);
                }
                case SUCCESS -> {
                    return success.preparePlaceholder(text, key);
                }
                case ERROR -> {
                    return error.preparePlaceholder(text, key);
                }
                case NONE -> {
                    return text;
                }
            }

            return text;
        }
    }

    private Style style;

    public Style getStyle() {
        return style;
    }

    public enum Level {
        INFO, SUCCESS, ERROR, NONE
    }

    public MessageBase(ConfigurationSection styleSection, ConfigurationSection messagesSection) {
        this.messagesSection = messagesSection;

        if (styleSection == null)
            return;

        this.style = new Style(styleSection);
    }

    public ConfigurationSection getSection() {
        return messagesSection;
    }

    private final static Pattern HEX_PATTERN = Pattern
            .compile("&(#[a-f0-9]{6})", Pattern.CASE_INSENSITIVE);

    public static String translateColor(Level level, String value, MessagePlaceholders messagePlaceholders) {

        Matcher m = HEX_PATTERN.matcher(value);
        while (m.find()) {
            value = value.replace(m.group(), ChatColor.of(m.group(1)).toString());
        }


        String tran = ChatColor.translateAlternateColorCodes('&', value);
        if (messagePlaceholders == null) {
            return tran;
        }
        value = messagePlaceholders.apply(level, tran);
        return PlaceholderAPI.setPlaceholders(messagePlaceholders.getPlayer(), value);
    }

    public CompletableFuture<Void> sendMessageIdAsync(Level level, Object listener, String idWithPlace, String def, MessagePlaceholders messagePlaceholders) {
        String id = null;
        if (idWithPlace.contains("|")) {
            String[] splitted = idWithPlace.split("\\|");
            id = splitted[0];
            if (splitted.length > 1) {
                String[] pl = splitted[1].split(",");
                for(String p : pl) {
                    String[] keyValue = p.split(":");
                    if(keyValue.length == 2) {
                        messagePlaceholders.put(keyValue[0], keyValue[1]);
                    }
                }
            }
        } else {
            id = idWithPlace;
        }

        @NotNull List<String> messages = getSection().getStringList(id);
        if (messages.isEmpty()) {
            messages = List.of(def);
        }


        for (String message : messages) {
            displayMessage(level, listener, makeMessage(level, message, messagePlaceholders));
        }
        return CompletableFuture.completedFuture(null);
    }

    public static String makeMessage(Level level, String message, MessagePlaceholders messagePlaceholders) {
        return translateColor(level, message, messagePlaceholders);
    }

    public String makeMessageId(Level level, String messageId, MessagePlaceholders messagePlaceholders) {
        List<String> messages = getSection().getStringList(messageId);
        String message;
        if (messages.isEmpty())
            message = getSection().getString(messageId);
        else
            message = messages.get(0);
        return translateColor(level, message, messagePlaceholders);
    }


    public abstract void displayMessage(Level level, Object player, String messages);

    public final CompletableFuture<Void> sendMessageIdAsync(Level level, Object player, String id, String def) {
        return sendMessageIdAsync(level, player, id, def, new MessagePlaceholders(player instanceof Player ? (Player) player : null));
    }

    public final CompletableFuture<Void> sendMessageIdAsync(Level level, Object player, String id, MessagePlaceholders messagePlaceholders) {
        return sendMessageIdAsync(level, player, id, "", messagePlaceholders);
    }

    public final CompletableFuture<Void> sendMessageIdAsync(Level level, Object player, String id) {
        return sendMessageIdAsync(level, player, id, "", new MessagePlaceholders(player instanceof Player ? (Player) player : null));
    }

    public void broadcastAll(Level level, OfflinePlayer sender, String id, String def, MessagePlaceholders messagePlaceholders) {
        broadcastFilter(level, sender, id, def, messagePlaceholders, (p) -> true);
    }

    public void broadcastFilter(Level level, OfflinePlayer sender,
                                String id,
                                String def,
                                MessagePlaceholders messagePlaceholders,
                                Predicate<? super Player> filter) {
        Bukkit.getOnlinePlayers().stream().filter(filter).forEach(p -> {
            if (sender != null)
                if (sender.getUniqueId().equals(p.getUniqueId()))
                    return;
            sendMessageIdAsync(level, p, id, def, messagePlaceholders);
        });
    }
}
