package ru.ycoord.core.commands.requirements;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.ycoord.YcoordCore;
import ru.ycoord.core.commands.Command;
import ru.ycoord.core.commands.HelpCommand;
import ru.ycoord.core.messages.MessageBase;
import ru.ycoord.core.messages.MessagePlaceholders;

import java.util.LinkedList;
import java.util.List;

public class SubcommandRequirement extends Requirement {

    private final List<Command> subcommands = new LinkedList<Command>();

    public SubcommandRequirement(Command command, List<Command> subcommands) {
        super(command);
        this.subcommands.add(new HelpCommand(command));
        this.subcommands.addAll(subcommands);
    }

    @Override
    protected List<String> subCompleteNext() {

        String arg = command.getPrevCompletionArg();
        List<String> args = command.getCompleteArgs();
        int counter = command.getCompleteArgsCounter();
        for (Command command : subcommands) {
            if (command.getName().equalsIgnoreCase(arg))
                return command.complete(this.command.getSender(), args.subList(counter, args.size()));
        }

        return super.subCompleteNext();
    }

    @Override
    public List<String> subComplete() {
        return subcommands.stream().filter(p -> p.canExecute(command.getSender())).map(Command::getName).toList();
    }

    public List<Command> getSubcommands() {
        return this.subcommands;
    }

    @Override
    public Object validate(CommandSender sender, String param) {

        List<Command> subCommands = subcommands;
        for (Command command : subCommands) {
            if (command.getName().equalsIgnoreCase(param))
                return command;
        }

        if (command.getSender() instanceof Player player) {
            MessagePlaceholders placeholders = new MessagePlaceholders(player);
            placeholders.put("%command%", param);
            placeholders.put("%possible%", commandsToString(sender));

            YcoordCore.getInstance().getChatMessage().sendMessageIdAsync(MessageBase.Level.ERROR,player, "messages.no-subcommand", placeholders);
        }
        return null;
    }

    private String commandsToString(CommandSender sender) {
        StringBuilder possible = new StringBuilder();

        for (Command c : subcommands) {
            if (c.canExecute(sender))
                possible.append(c.getName()).append(", ");
        }
        String possibleString = possible.substring(0, possible.length() - 2);
        return String.format("[%s]", possibleString);
    }

    @Override
    public void failed(CommandSender sender) {
        if (sender instanceof Player player) {
            MessagePlaceholders placeholders = new MessagePlaceholders(player);
            placeholders.put("%possible%", commandsToString(sender));

            YcoordCore.getInstance().getChatMessage().sendMessageIdAsync(MessageBase.Level.ERROR,player, "messages.subcommand-error", placeholders);

        }
    }

    @Override
    public void sendDescription(CommandSender sender) {
        if(sender instanceof Player player) {
            for (Command c : subcommands) {
                MessagePlaceholders placeholders = new MessagePlaceholders(player);
                placeholders.put("%command%", c.getName());
                placeholders.put("%description%", c.getDescription(sender));
                YcoordCore.getInstance().getChatMessage().sendMessageIdAsync(MessageBase.Level.INFO,player, "messages.help-item-info", placeholders);
            }
        }
    }

    public void addCommand(Command command) {
        subcommands.removeIf(c->c.getClass().equals(command.getClass()));
        subcommands.add(command);
    }
}
