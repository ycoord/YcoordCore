package ru.ycoord.core.balance;

import org.bukkit.entity.Player;

public class ExpBalance extends Balance{
    @Override
    void subWithdraw(Player player, double money) {
        int level = player.getTotalExperience();
        player.setTotalExperience((int) (level-money));
    }

    @Override
    void subDeposit(Player player, double money) {
        int level = player.getTotalExperience();
        player.setTotalExperience((int) (level+money));
    }

    @Override
    double subGet(Player player) {
        return player.getTotalExperience();
    }
}
