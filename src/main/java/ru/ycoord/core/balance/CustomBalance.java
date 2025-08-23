package ru.ycoord.core.balance;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import ru.ycoord.core.messages.MessageBase;
import ru.ycoord.core.messages.MessagePlaceholders;
import ru.ycoord.core.utils.Utils;

public class CustomBalance extends Balance {
    private final ConfigurationSection section;

    public CustomBalance(ConfigurationSection section) {
        super(section);
        this.section = section;
    }
    @Override
    public void subWithdraw(Player player, int money) {
        String withDrawCommand = section.getString("take");
        MessagePlaceholders placeholders = new MessagePlaceholders(player);
        placeholders.put("%count%", money);
        placeholders.put("%player%", player.getName());
        Utils.executeConsole(MessageBase.makeMessage(MessageBase.Level.NONE, withDrawCommand, placeholders));
    }

    @Override
    public void subDeposit(Player player, int money) {
        String depositCommand = section.getString("give");
        MessagePlaceholders placeholders = new MessagePlaceholders(player);
        placeholders.put("%count%", money);
        placeholders.put("%player%", player.getName());
        Utils.executeConsole(MessageBase.makeMessage(MessageBase.Level.NONE, depositCommand, placeholders));
    }

    @Override
    public int subGet(Player player) {
        String getPlaceholder = section.getString("get");
        assert getPlaceholder != null;
        String result = PlaceholderAPI.setPlaceholders(player, getPlaceholder.replace("%player%", player.getName()));
        return Integer.parseInt(result);
    }
}
