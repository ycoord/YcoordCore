package ru.ycoord.core.messages;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.ycoord.YcoordCore;
import ru.ycoord.core.sound.SoundInfo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

public abstract class MessageBase {
    protected ConfigurationSection messagesSection;
    protected boolean useDefaultSound = false;
    protected SoundInfo defaultSound;

    public MessageBase(YcoordCore core, ConfigurationSection messagesSection) {
        this.messagesSection = messagesSection;

        ConfigurationSection config = core.getConfig();
        ConfigurationSection messagesSettings = config.getConfigurationSection("messages-settings");
        if (messagesSettings == null)
            return;

        ConfigurationSection soundSettings = messagesSettings.getConfigurationSection("sound");
        if (soundSettings == null)
            return;


        this.useDefaultSound = soundSettings.getBoolean("use-default", true);
        if (this.useDefaultSound) {
            ConfigurationSection defaultSoundSection = soundSettings.getConfigurationSection("default-sound");
            if (defaultSoundSection == null)
                this.useDefaultSound = false;
            else
                this.defaultSound = new SoundInfo(defaultSoundSection);
        }
    }

    public ConfigurationSection getSection() {
        return messagesSection;
    }

    public static String convertTime(Long milli) {
        return convertTime(milli, "HH:mm:ss dd-MM-yyyy");
    }

    public static String convertTime(Long milli, String pattern) {
        Date currentDate = new Date(milli);
        DateFormat df = new SimpleDateFormat(pattern);
        return df.format(currentDate);
    }

    public static String translateColor(String value, MessagePlaceholders messagePlaceholders) {
        String tran = ChatColor.translateAlternateColorCodes('&', value);
        if (messagePlaceholders == null) {
            return tran;
        }
        value = messagePlaceholders.apply(tran);
        return PlaceholderAPI.setPlaceholders(messagePlaceholders.getPlayer(), value);
    }

    public void sendMessageId(OfflinePlayer player, String id, String def, MessagePlaceholders messagePlaceholders) {
        @NotNull List<String> messages = getSection().getStringList(id);
        if (messages.isEmpty()) {
            messages = List.of(def);
        }


        for (String message : messages) {
            displayMessage(player, makeMessage(message, messagePlaceholders));
        }
    }

    public static String makeMessage(String message, MessagePlaceholders messagePlaceholders) {
        return translateColor(message, messagePlaceholders);
    }

    public String makeMessageId(String messageId, MessagePlaceholders messagePlaceholders) {
        List<String> messages = getSection().getStringList(messageId);
        String message = null;
        if (messages.isEmpty())
            message = getSection().getString(messageId);
        else
            message = messages.get(0);
        return translateColor(message, messagePlaceholders);
    }


    public abstract void displayMessage(OfflinePlayer player, String messages);

    public final void sendMessageId(OfflinePlayer player, String id, String def) {
        sendMessageId(player, id, def, new MessagePlaceholders(player));
    }

    public final void sendMessageId(OfflinePlayer player, String id, MessagePlaceholders messagePlaceholders) {
        sendMessageId(player, id, "", messagePlaceholders);
    }

    public final void sendMessageId(OfflinePlayer player, String id) {
        sendMessageId(player, id, "", new MessagePlaceholders(player));
    }

    public void broadcastAll(OfflinePlayer sender, String id, String def, MessagePlaceholders messagePlaceholders) {
        broadcastFilter(sender, id, def, messagePlaceholders, (p) -> true);
    }

    public void broadcastFilter(OfflinePlayer sender,
                                String id,
                                String def,
                                MessagePlaceholders messagePlaceholders,
                                Predicate<? super Player> filter) {
        Bukkit.getOnlinePlayers().stream().filter(filter).forEach(p -> {
            if (sender != null)
                if (sender.getUniqueId().equals(p.getUniqueId()))
                    return;
            sendMessageId(p, id, def, messagePlaceholders);
        });
    }
}
