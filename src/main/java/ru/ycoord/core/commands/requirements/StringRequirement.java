package ru.ycoord.core.commands.requirements;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.ycoord.YcoordCore;
import ru.ycoord.core.commands.Command;

import java.util.List;

public class StringRequirement extends Requirement {
    private String completionId = null;
    public StringRequirement(Command command) {
        super(command);
    }

    public StringRequirement(Command command, String completionId) {
        super(command);
        this.completionId = completionId;
    }

    @Override
    public List<String> subComplete() {
        if(completionId != null)
            return YcoordCore.getInstance().getConfig().getStringList(completionId);
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
            YcoordCore.getInstance().getChatMessage().sendMessageId(player, "messages.string-error");
        }
    }

    @Override
    public void sendDescription(CommandSender sender) {
        if (sender instanceof Player player)
        {
            YcoordCore.getInstance().getChatMessage().sendMessageId(player, "messages.string-description");
        }
    }
}
