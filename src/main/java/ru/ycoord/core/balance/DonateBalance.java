package ru.ycoord.core.balance;

import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DonateBalance extends Balance {
    private final PlayerPointsAPI pp;

    public DonateBalance(@NotNull PlayerPointsAPI pp) {
        this.pp = pp;
    }

    @Override
    public void subWithdraw(Player player, double money) {
        pp.take(player.getUniqueId(), (int) money);
    }

    @Override
    public void subDeposit(Player player, double money) {
        pp.give(player.getUniqueId(), (int) money);
    }

    @Override
    public double subGet(Player player) {
        return pp.look(player.getUniqueId());
    }
}
