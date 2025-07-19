package ru.ycoord.core.commands.requirements;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.ycoord.core.commands.Command;
import ru.ycoord.core.messages.MessagePlaceholders;

import java.util.List;

public class OnlinePlayerRequirement extends Requirement {

    public OnlinePlayerRequirement(Command command) {
        super(command);
    }

    @Override
    public List<String> subComplete() {

        return Bukkit.getOnlinePlayers().stream().map(e->e.getName()).toList();
    }

    @Override
    public Object validate(CommandSender sender, String param) {
        Object value = Bukkit.getPlayer(param);
        if(value == null)
        {
            if (command.getSender() instanceof Player player) {

                MessagePlaceholders placeholders = new MessagePlaceholders(player);
                placeholders.put("%player%", param);
                messageBase.sendMessageId(player, "messages.player-offline", placeholders);

            }
        }

        return value;
    }

    @Override
    public void failed(CommandSender sender) {
        if (sender instanceof Player player)
        {
            messageBase.sendMessageId(player, "messages.online-player-error");
        }
    }

    @Override
    public void sendDescription(CommandSender sender) {
        if (sender instanceof Player player)
        {
            messageBase.sendMessageId(player, "messages.online-description");
        }
    }
}
