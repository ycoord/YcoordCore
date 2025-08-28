package ru.ycoord.core.messages;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import ru.ycoord.YcoordCore;
import ru.ycoord.core.parcticle.ParticlesInfo;
import ru.ycoord.core.sound.SoundInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ChatMessage extends MessageBase {
    public ChatMessage(ConfigurationSection styleSection, ConfigurationSection messagesSection) {
        super(styleSection, messagesSection);
    }

    public void sendMessageId(Level level, Object player, String messageId, String def, final MessagePlaceholders messagePlaceholders){

            if (player == null)
                return;

            CommandSender sender = null;
            if(player instanceof CommandSender commandSender){
                sender = commandSender;
            }

            if(sender == null)
                return;

            if (!messagesSection.contains(messageId)) {
                String newdef = messageId + "|" + String.join(", ", messagePlaceholders.keySet()).replace("%", "") + ":" + def;

                sender.sendMessage(translateColor(level, newdef, messagePlaceholders));
                return;
            }
            List<?> messages = messagesSection.getList(messageId);

            assert messages != null;

            boolean hasSound = false;
            for (Object message : messages) {
                if (message instanceof String stringMessage) {
                    sender.sendMessage(translateColor(level, stringMessage, messagePlaceholders));
                } else {
                    if (message instanceof HashMap<?, ?> map) {
                        for (Map.Entry<?, ?> entry : map.entrySet()) {
                            if (entry.getValue() instanceof HashMap<?, ?> valueMap) {
                                if (!valueMap.containsKey("type")) {
                                    continue;
                                }

                                String type = (String) valueMap.get("type");
                                YamlConfiguration yamlConfiguration = new YamlConfiguration();
                                yamlConfiguration.createSection("complex", valueMap);
                                ConfigurationSection section = yamlConfiguration.getConfigurationSection("complex");
                                if (section == null) {
                                    continue;
                                }

                                switch (type.toLowerCase()) {
                                    case "sound": {
                                        SoundInfo info = new SoundInfo(section);
                                        if(sender instanceof Player p) {
                                            info.play(p);
                                        }
                                        hasSound = true;
                                        break;
                                    }
                                    case "particle": {
                                        ParticlesInfo info = new ParticlesInfo(section);
                                        if(sender instanceof Player p) {
                                            info.play(p);
                                        }

                                        break;
                                    }
                                    case "component": {
                                        Component component = Component.empty();
                                        Map<?, ?> parts = (Map<?, ?>) valueMap.get("parts");

                                        for (Map.Entry<?, ?> partEntry : parts.entrySet()) {
                                            if (partEntry.getValue() instanceof Map<?, ?> partsMap) {
                                                if (!partsMap.containsKey("text")) {
                                                    continue;
                                                }
                                                String text = (String) partsMap.get("text");
                                                TextComponent.Builder builder = Component.text().content(translateColor(level, text, messagePlaceholders));

                                                if (partsMap.containsKey("hover")) {
                                                    String hover = (String) partsMap.get("hover");
                                                    hover = messagePlaceholders.apply(Level.INFO, hover);
                                                    builder.hoverEvent(Component.text(translateColor(level, hover, messagePlaceholders)));
                                                }

                                                if (partsMap.containsKey("commands")) {
                                                    List<?> todo = (List<?>) partsMap.get("commands");

                                                    for (Object c : todo) {
                                                        if (c instanceof String command) {
                                                            String r = command;
                                                            r = messagePlaceholders.apply(level, r);
                                                            builder.clickEvent(ClickEvent.runCommand(r));
                                                        }
                                                    }
                                                }

                                                if (partsMap.containsKey("open")) {
                                                    String todo = (String) partsMap.get("open");
                                                    builder.clickEvent(ClickEvent.openUrl(todo));
                                                }

                                                if (partsMap.containsKey("copy")) {
                                                    String todo = (String) partsMap.get("copy");
                                                    builder.clickEvent(ClickEvent.copyToClipboard(todo));
                                                }

                                                component = component.append(builder.asComponent());
                                            }
                                        }
                                        sender.sendMessage(component);
                                    }
                                }
                            }

                        }
                    }
                }
            }
            if (!hasSound) {
                MessageBase.Style style = YcoordCore.getInstance().getChatMessage().getStyle();
                if (style != null) {
                    if(sender instanceof Player p) {
                        style.playSound(level, p);
                    }
                }
            }

    }

    @Override
    public CompletableFuture<Void> sendMessageIdAsync(Level level, Object player, String idWithPlace, String def, MessagePlaceholders messagePlaceholders) {


        if (messagePlaceholders == null) {
            messagePlaceholders = new MessagePlaceholders(null);
        }

        String messageId;
        if (idWithPlace.contains("|")) {
            String[] splitted = idWithPlace.split("\\|");
            messageId = splitted[0];
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
            messageId = idWithPlace;
        }

        MessagePlaceholders finalPlaceholders = messagePlaceholders;
        String finalMessageId = messageId;
        return CompletableFuture.runAsync(() -> sendMessageId(level, player, finalMessageId, def, finalPlaceholders));
    }

    @Override
    public void displayMessage(Level level, Object player, String messages) {

    }
}
