package ru.ycoord.core.commands;

import org.bukkit.command.CommandSender;

public abstract class NoPermissionCommand extends Command{
    @Override
    public boolean canExecute(CommandSender sender) {
        return true;
    }
}
