package ru.ycoord.core.commands.requirements;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.ycoord.YcoordCore;
import ru.ycoord.core.commands.Command;
import ru.ycoord.core.messages.MessageBase;

import java.util.List;

public class IntegerRequirement extends Requirement {

    public IntegerRequirement(Command command) {
        super(command);
    }

    @Override
    public List<String> subComplete() {
        return YcoordCore.getInstance().getConfig().getStringList("default-int-completion");
    }

    @Override
    public Object validate(CommandSender sender, String param) {

        try {
            return Integer.parseInt(param);
        } catch (NumberFormatException ex) {
            if (command.getSender() instanceof Player player) {
                YcoordCore.getInstance().getChatMessage().sendMessageId(MessageBase.Level.ERROR,player, "messages.not-integer");

            }
            return null;
        }
    }

    @Override
    public void failed(CommandSender sender) {
        if (sender instanceof Player player) {
            YcoordCore.getInstance().getChatMessage().sendMessageId(MessageBase.Level.ERROR,player, "messages.integer-error");
        }
    }

    @Override
    public void sendDescription(CommandSender sender) {
        if (sender instanceof Player player)
        {
            YcoordCore.getInstance().getChatMessage().sendMessageId(MessageBase.Level.ERROR,player, "messages.integer-description");
        }
    }
}
