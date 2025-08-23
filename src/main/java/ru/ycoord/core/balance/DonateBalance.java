package ru.ycoord.core.balance;

import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DonateBalance extends Balance {
    private final PlayerPointsAPI pp;

    public DonateBalance(ConfigurationSection moneyCfg, @NotNull PlayerPointsAPI pp) {
        super(moneyCfg);
        this.pp = pp;
    }

    @Override
    public void subWithdraw(Player player, int money) {
        pp.take(player.getUniqueId(), (int) money);
    }

    @Override
    public void subDeposit(Player player, int money) {
        pp.give(player.getUniqueId(), (int) money);
    }

    @Override
    public int subGet(Player player) {
        return pp.look(player.getUniqueId());
    }
}
