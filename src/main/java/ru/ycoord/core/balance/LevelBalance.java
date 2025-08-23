package ru.ycoord.core.balance;

import org.bukkit.entity.Player;

public class LevelBalance extends Balance{
    @Override
    void subWithdraw(Player player, double money) {
        int level = player.getLevel();
        player.setLevel((int) (level-money));
    }

    @Override
    void subDeposit(Player player, double money) {
        int level = player.getLevel();
        player.setLevel((int) (level+money));
    }

    @Override
    double subGet(Player player) {
        return player.getLevel();
    }
}
