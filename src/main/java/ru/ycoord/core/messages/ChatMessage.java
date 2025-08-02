package ru.ycoord.core.messages;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import ru.ycoord.YcoordCore;
import ru.ycoord.core.parcticle.ParticlesInfo;
import ru.ycoord.core.sound.SoundInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatMessage extends MessageBase {
    public ChatMessage(YcoordCore core, ConfigurationSection messagesSection) {
        super(core, messagesSection);
    }

    @Override
    public void sendMessageId(OfflinePlayer player, String messageId, String def, MessagePlaceholders messagePlaceholders) {
        if (player == null)
            return;
        if (!player.isOnline() || player.getPlayer() == null)
            return;

        if (messagePlaceholders == null) {
            messagePlaceholders = new MessagePlaceholders(null);
        }

        if (!messagesSection.contains(messageId)) {
            def = messageId + "|" + String.join(", ", messagePlaceholders.keySet()).replace("%", "") + ":" + def;

            player.getPlayer().sendMessage(translateColor(def, messagePlaceholders));
            return;
        }
        List<?> messages = messagesSection.getList(messageId);

        assert messages != null;

        boolean hasSound = false;
        for (Object message : messages) {
            if (message instanceof String stringMessage) {
                player.getPlayer().sendMessage(translateColor(stringMessage, messagePlaceholders));
            } else {
                if (message instanceof HashMap<?, ?> map) {
                    for (Map.Entry<?, ?> entry : map.entrySet()) {
                        if (entry.getValue() instanceof HashMap<?, ?> valueMap) {
                            if (!valueMap.containsKey("type")) {
                                continue;
                            }

                            String type = (String) valueMap.get("type");
                            YamlConfiguration yamlConfiguration = new YamlConfiguration();
                            yamlConfiguration.createSection("complex", (Map<?, ?>) valueMap);
                            ConfigurationSection section = yamlConfiguration.getConfigurationSection("complex");
                            if (section == null) {
                                continue;
                            }

                            switch (type.toLowerCase()) {
                                case "sound": {
                                    SoundInfo info = new SoundInfo(section);
                                    info.play(player);
                                    hasSound = true;
                                    break;
                                }
                                case "particle": {
                                    ParticlesInfo info = new ParticlesInfo(section);
                                    info.play(player);
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
                                            TextComponent.Builder builder = Component.text().content(translateColor(text, messagePlaceholders));

                                            if (partsMap.containsKey("hover")) {
                                                String hover = (String) partsMap.get("hover");
                                                hover = messagePlaceholders.apply(hover);
                                                builder.hoverEvent(Component.text(translateColor(hover, messagePlaceholders)));
                                            }

                                            if (partsMap.containsKey("commands")) {
                                                List<?> todo = (List<?>) partsMap.get("commands");

                                                for (Object c : todo) {
                                                    if (c instanceof String command) {
                                                        String r = command;
                                                        r = messagePlaceholders.apply(r);
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
                                    player.getPlayer().sendMessage(component);
                                }
                            }
                        }

                    }
                }
            }
        }
        if (!hasSound && useDefaultSound) {
            defaultSound.play(player);
        }
    }

    @Override
    public void displayMessage(OfflinePlayer player, String messages) {

    }
}
