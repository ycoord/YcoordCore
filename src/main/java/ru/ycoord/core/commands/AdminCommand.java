package ru.ycoord.core.commands;

import org.bukkit.command.CommandSender;

public abstract class AdminCommand extends Command{
    @Override
    public boolean canExecute(CommandSender sender) {
        return super.canExecute(sender) && sender.isOp();
    }
}
