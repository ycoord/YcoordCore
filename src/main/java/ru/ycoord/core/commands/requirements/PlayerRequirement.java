package ru.ycoord.core.commands.requirements;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import ru.ycoord.YcoordCore;
import ru.ycoord.core.commands.Command;
import ru.ycoord.core.messages.MessageBase;

import java.util.List;

public class PlayerRequirement extends Requirement {

    public PlayerRequirement(Command command) {
        super(command);
    }

    @Override
    public List<String> subComplete() {

        return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).toList();
    }

    @Override
    public Object validate(CommandSender sender,String param) {
        return Bukkit.getOfflinePlayer(param);
    }

    @Override
    public void failed(CommandSender sender) {
        if (sender instanceof Player player)
        {
            YcoordCore.getInstance().getChatMessage().sendMessageId(MessageBase.Level.ERROR,player, "messages.player-error");
        }
    }

    @Override
    public void sendDescription(CommandSender sender) {
        if (sender instanceof Player player)
        {
            YcoordCore.getInstance().getChatMessage().sendMessageId(MessageBase.Level.ERROR,player, "messages.offline-description");
        }
    }
}
