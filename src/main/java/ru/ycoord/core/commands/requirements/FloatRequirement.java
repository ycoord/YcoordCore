package ru.ycoord.core.commands.requirements;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.ycoord.YcoordCore;
import ru.ycoord.core.commands.Command;

import java.util.List;

public class FloatRequirement extends Requirement {

    public FloatRequirement(Command command) {
        super(command);
    }


    @Override
    public List<String> subComplete() {
        return YcoordCore.getInstance().getConfig().getStringList("default-float-completion");
    }

    @Override
    public void failed(CommandSender sender) {
        if (sender instanceof Player player)
        {
            YcoordCore.getInstance().getChatMessage().sendMessageId(player, "messages.float-error");
        }
    }

    @Override
    public void sendDescription(CommandSender sender) {
        if (sender instanceof Player player)
        {
            YcoordCore.getInstance().getChatMessage().sendMessageId(player, "messages.float-description");
        }
    }

    @Override
    public Object validate(CommandSender sender, String param) {
        try {
            return Float.parseFloat(param);
        }catch (NumberFormatException ex){
            if (command.getSender() instanceof Player player) {
                YcoordCore.getInstance().getChatMessage().sendMessageId(player, "messages.not-float");

            }
            return null;
        }
    }
}

