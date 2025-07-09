package ru.ycoord.core.commands.requirements;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.ycoord.YcoordCore;
import ru.ycoord.core.commands.Command;

import java.util.List;

public class StringRequirement extends Requirement {

    public StringRequirement(Command command) {
        super(command);
    }

    @Override
    public List<String> subComplete() {
        return YcoordCore.getInstance().getConfig().getStringList("default-string-completion");
    }

    @Override
    public Object validate(CommandSender player, String param) {
        return param;
    }

    @Override
    public void failed(CommandSender sender) {
        if (sender instanceof Player player)
        {
            messageBase.sendMessageId(player, "messages.string-error");
        }
    }
}
