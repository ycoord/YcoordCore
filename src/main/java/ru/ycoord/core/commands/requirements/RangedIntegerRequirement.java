package ru.ycoord.core.commands.requirements;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.ycoord.YcoordCore;
import ru.ycoord.core.commands.Command;
import ru.ycoord.core.messages.MessageBase;
import ru.ycoord.core.messages.MessagePlaceholders;

import java.util.List;

public class RangedIntegerRequirement extends IntegerRequirement {
    private final int min;
    private final int max;

    public RangedIntegerRequirement(Command command, int min, int max) {
        super(command);
        this.min = min;
        this.max = max;
    }

    @Override
    public List<String> subComplete() {
        return List.of(String.format("[%d...%d]", min, max));
    }

    @Override
    public Object validate(CommandSender sender,String param) {

        Object value = super.validate(sender, param);
        if (value == null)
            return null;

        if (value instanceof Integer valueint) {
            if (min <= valueint && valueint <= max)
                return valueint;
        }

        if (command.getSender() instanceof Player player) {
            MessagePlaceholders placeholders = new MessagePlaceholders(player);
            placeholders.put("%min%", min);
            placeholders.put("%max%", max);
            YcoordCore.getInstance().getChatMessage().sendMessageIdAsync(MessageBase.Level.ERROR,player, "messages.no-range", placeholders);
        }

        return null;
    }


    @Override
    public void failed(CommandSender sender) {
        if (sender instanceof Player player)
        {
            MessagePlaceholders placeholders = new MessagePlaceholders(player);
            placeholders.put("%min%", min);
            placeholders.put("%max%", max);
            YcoordCore.getInstance().getChatMessage().sendMessageIdAsync(MessageBase.Level.ERROR,player, "messages.ranged-error", placeholders);

        }
    }
}
