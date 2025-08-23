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
        this.section = section;
    }
    @Override
    public void subWithdraw(Player player, double money) {
        String withDrawCommand = section.getString("withdraw");
        MessagePlaceholders placeholders = new MessagePlaceholders(player);
        placeholders.put("%amount%", money);
        Utils.executeConsole(MessageBase.makeMessage(MessageBase.Level.NONE, withDrawCommand, placeholders));
    }

    @Override
    public void subDeposit(Player player, double money) {
        String depositCommand = section.getString("deposit");
        MessagePlaceholders placeholders = new MessagePlaceholders(player);
        placeholders.put("%amount%", money);
        Utils.executeConsole(MessageBase.makeMessage(MessageBase.Level.NONE, depositCommand, placeholders));
    }

    @Override
    public double subGet(Player player) {
        String getPlaceholder = section.getString("get");
        assert getPlaceholder != null;
        String result = PlaceholderAPI.setPlaceholders(player, getPlaceholder.replace("%player%", player.getName()));
        return Double.parseDouble(result);
    }
}
