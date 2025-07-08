package ru.ycoord.balance;

import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DonateBalance implements IBalance {
    private final PlayerPointsAPI pp;

    public DonateBalance(@NotNull PlayerPointsAPI pp) {
        this.pp = pp;
    }

    @Override
    public void withdraw(Player player, double money) {
        pp.take(player.getUniqueId(), (int) money);
    }

    @Override
    public void deposit(Player player, double money) {
        pp.give(player.getUniqueId(), (int) money);
    }

    @Override
    public double get(Player player) {
        return pp.look(player.getUniqueId());
    }
}
