package ru.ycoord.core.balance;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MoneyBalance extends Balance {
    @NotNull
    private final Economy provider;

    public MoneyBalance(ConfigurationSection moneyCfg, @NotNull Economy provider) {
        super(moneyCfg);
        this.provider = provider;
    }

    @Override
    public void subWithdraw(Player player, int money) {
        provider.withdrawPlayer(player, money);
    }

    @Override
    public void subDeposit(Player player, int money) {
        provider.depositPlayer(player, money);
    }

    @Override
    public int subGet(Player player) {
        return (int) provider.getBalance(player);
    }
}
