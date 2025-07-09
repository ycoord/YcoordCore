package ru.ycoord.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.ycoord.YcoordCore;
import ru.ycoord.messages.MapMessages;
import ru.ycoord.messages.MessagePlaceholders;

import java.util.Map;

public class CoreCommand implements CommandExecutor {
    public YcoordCore ycoordCore;
    public CoreCommand(YcoordCore ycoordCore) {
        this.ycoordCore = ycoordCore;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(commandSender instanceof Player player)
        {
            if(strings[0].equalsIgnoreCase("message"))
            {
                MessagePlaceholders placeholders = new MapMessages(player, Map.of(
                        "%some%", "some placeholder"
                ));
                if(strings[1].equalsIgnoreCase("simple"))
                {
                    ycoordCore.getChatMessage().sendMessageId(player, "messages.default-message", placeholders);
                }
                else if(strings[1].equalsIgnoreCase("complex"))
                {
                    ycoordCore.getChatMessage().sendMessageId(player, "messages.sound-placeholder-particle-command-hover", placeholders);
                }
            }
        }
        return true;
    }
}
