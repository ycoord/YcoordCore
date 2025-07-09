package ru.ycoord.core.commands.requirements;

import org.bukkit.command.CommandSender;
import ru.ycoord.YcoordCore;
import ru.ycoord.core.commands.Command;
import ru.ycoord.core.messages.MessageBase;

import java.util.List;

public abstract class Requirement {
    protected final Command command;
    protected final MessageBase messageBase;
    public Requirement(Command command) {
        this.command = command;
        this.messageBase = YcoordCore.getInstance().getChatMessage();
    }


    public Object require(CommandSender player) {
        String value = command.hasNext();
        if (value == null) {
            return null;
        }

        return validate(player, value);
    }


    public final List<String> complete(CommandSender sender) throws IllegalArgumentException {

        if (command.shouldComplete()) {
            return subComplete();

        }

        String currentArg = command.geCurrentCompletionArg();

        Object value = validate(sender, currentArg);
        if (value == null) {
            throw new IllegalArgumentException("невозможный параметр");
        }
        command.nextCompletion();
        List<String> next = subCompleteNext();
        if (next != null)
            return next;

        return null;
    }

    protected List<String> subCompleteNext() {
        return null;
    }

    public abstract Object validate(CommandSender player, String param);

    protected abstract List<String> subComplete();

    public abstract void failed(CommandSender sender);

    public boolean isOptional() {
        return false;
    }
}
