package ru.ycoord.core.commands;

import org.bukkit.command.CommandSender;
import ru.ycoord.core.commands.requirements.Requirement;

import java.util.List;

public class HelpCommand extends Command{
    private final Command command;

    public HelpCommand(Command command) {
        this.command = command;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args, List<Object> params) {
        if(!super.execute(sender, args, params))
            return false;


        List<Requirement> reqs =  command.getRequirements(sender);
        for(Requirement req : reqs){
            req.sendDescription(sender);
        }
        return true;
    }

    @Override
    public String getDescription(CommandSender sender) {
        return "информация о команде";
    }
}
