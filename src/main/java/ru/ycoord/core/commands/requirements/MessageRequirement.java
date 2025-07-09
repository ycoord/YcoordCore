package ru.ycoord.core.commands.requirements;

import org.bukkit.command.CommandSender;
import ru.ycoord.YcoordCore;
import ru.ycoord.core.commands.Command;

import java.util.List;
import java.util.StringJoiner;

public class MessageRequirement extends StringRequirement{
    public MessageRequirement(Command command) {
        super(command);
    }

    @Override
    public List<String> subComplete() {
        return YcoordCore.getInstance().getConfig().getStringList("default-message-completion");
    }

    public final Object require(CommandSender player) {
        return validate(player, null);
    }

    @Override
    public Object validate(CommandSender player, String param) {
        List<String> args;

        if(command.getArgs() == null)
            return param;

        args = command.getArgs().subList(command.getArgCounter(), command.getArgs().size());

        StringJoiner joined = new StringJoiner(" ");
        for(String arg : args)
            joined.add(arg);


        return joined.toString();
    }
}
