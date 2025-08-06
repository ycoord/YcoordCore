package ru.ycoord.core.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.ycoord.YcoordCore;
import ru.ycoord.core.commands.requirements.Requirement;
import ru.ycoord.core.messages.MessageBase;

import java.util.List;

public abstract class Command {
    private int argCounter = 0;
    private int paramCounter = 0;

    private int completionCounter = 0;
    private List<String> args;
    private List<String> completeArgs;
    private CommandSender sender;
    private List<Object> params;

    public Command() {

    }

    public List<Requirement> getRequirements(CommandSender sender) {
        return List.of();
    }

    public boolean canPlayerExecute() {
        return true;
    }

    public boolean canExecute(CommandSender sender) {
        return sender.hasPermission("*");

    }

    public abstract String getName();


    protected void resetCounter() {
        argCounter = 0;
        paramCounter = 0;
    }

    public CommandSender getSender() {
        return sender;
    }


    protected List<String> completeEnd() {
        return List.of();
    }


    public List<String> complete(CommandSender sender, List<String> args) {

        if (!canExecute(sender)) {
            return completeEnd();
        }

        completionCounter = 0;
        completeArgs = args;
        this.sender = sender;
        List<Requirement> reqs = getRequirements(sender);
        for (Requirement req : reqs) {

            try {
                List<String> result = req.complete(sender);
                if (result != null)
                    return result;
            } catch (IllegalArgumentException ex) {
                return completeEnd();
            }

        }

        return completeEnd();
    }

    protected void unknownCommand(CommandSender sender) {
        if (sender instanceof Player player)
            YcoordCore.getInstance().getChatMessage().sendMessageId(MessageBase.Level.ERROR, player, "messages.unknown-command", "неизвестная команда", null);
    }

    protected void noPermission(CommandSender sender) {
        if (sender instanceof Player player)
            YcoordCore.getInstance().getChatMessage().sendMessageId(MessageBase.Level.ERROR, player, "messages.no-permission", "нет прав", null);
    }

    protected boolean unknownCommandIfCantExecute() {
        return true;
    }

    public boolean execute(CommandSender sender, List<String> args, List<Object> params) {

        if (!canExecute(sender)) {
            if (unknownCommandIfCantExecute())
                unknownCommand(sender);
            else
                noPermission(sender);
            return false;
        }

        if (!canPlayerExecute() && (sender instanceof Player player)) {
            YcoordCore.getInstance().getChatMessage().sendMessageId(MessageBase.Level.ERROR,player, "messages.player-cant");

            return false;
        }

        resetCounter();
        this.args = args;
        this.sender = sender;
        this.params = params;


        List<Requirement> reqs = getRequirements(sender);
        for (Requirement req : reqs) {

            try {
                Object result = req.require(sender);
                if (result == null)
                    return false;

                if (result instanceof Command command) {
                    return executeNext(command, params);
                }

                params.add(result);
            } catch (IllegalArgumentException ex) {

                boolean optional = req.isOptional();
                if (!optional) {
                    req.failed(sender);
                    return false;
                }


                return true;
            }
        }
        return true;
    }

    private String getCurrentArg() {
        return args.get(argCounter);
    }

    protected void throwNotEnough() {
        throw new IllegalArgumentException("недостаточно аргументов");
    }

    protected boolean executeNext(Command command, List<Object> params) {
        return command.execute(sender, args.subList(argCounter, args.size()), params);
    }

    public String hasNext() throws IllegalArgumentException {
        if (args.size() <= argCounter) {
            throwNotEnough();
        }

        return args.get(argCounter++);
    }

    protected Object hasNextParam(boolean noParamOk) {
        if (params.size() <= paramCounter) {
            if (sender instanceof Player player) {
                if (noParamOk)
                    YcoordCore.getInstance().getChatMessage().sendMessageId(MessageBase.Level.ERROR,player, "messages.need-param");
            }

            return null;
        }

        return params.get(paramCounter++);
    }

    protected <T> T getParam() {
        return getParam(false);
    }

    protected <T> T getParam(boolean noParamOk) {

        Object result = hasNextParam(noParamOk);
        if (result != null)
            return (T) result;

        return null;
    }


    public boolean shouldComplete() {

        return completeArgs.size() == completionCounter + 1;
    }

    public void nextCompletion() {
        completionCounter++;
    }

    public String getPrevCompletionArg() {
        return completeArgs.get((completionCounter - 1));
    }

    public String geCurrentCompletionArg() {
        return completeArgs.get((completionCounter));
    }

    public List<String> getCompleteArgs() {
        return completeArgs;
    }

    public List<String> getArgs() {
        return args;
    }

    public int getCompleteArgsCounter() {
        return completionCounter;
    }

    public int getArgCounter() {
        return argCounter;
    }

    public abstract String getDescription(CommandSender sender);
}
