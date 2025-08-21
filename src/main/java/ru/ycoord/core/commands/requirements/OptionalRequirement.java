package ru.ycoord.core.commands.requirements;

import org.bukkit.command.CommandSender;
import ru.ycoord.core.commands.Command;

import java.util.List;

public class OptionalRequirement extends SubcommandRequirement {

    public OptionalRequirement(Command command, List<Command> subcommands) {
        super(command, subcommands);
    }

    @Override
    public boolean isOptional() {
        return true;
    }
}
