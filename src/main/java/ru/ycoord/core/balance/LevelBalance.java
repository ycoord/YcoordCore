package ru.ycoord.core.balance;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class LevelBalance extends Balance{
    public LevelBalance(ConfigurationSection moneyCfg) {
        super(moneyCfg);
    }

    @Override
    void subWithdraw(Player player, int money) {
        int level = player.getLevel();
        player.setLevel((int) (level-money));
    }

    @Override
    void subDeposit(Player player, int money) {
        int level = player.getLevel();
        player.setLevel((int) (level+money));
    }

    @Override
    int subGet(Player player) {
        return player.getLevel();
    }
}
