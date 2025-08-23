package ru.ycoord.core.balance;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MoneyBalance extends Balance {
    @NotNull
    private final Economy provider;

    public MoneyBalance(@NotNull Economy provider) {
        this.provider = provider;
    }

    @Override
    public void subWithdraw(Player player, double money) {
        provider.withdrawPlayer(player, money);
    }

    @Override
    public void subDeposit(Player player, double money) {
        provider.depositPlayer(player, money);
    }

    @Override
    public double subGet(Player player) {
        return provider.getBalance(player);
    }
}
