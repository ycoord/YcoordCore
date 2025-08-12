package ru.ycoord.core.balance;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MoneyBalance implements IBalance {
    @NotNull
    private final Economy provider;

    public MoneyBalance(@NotNull Economy provider) {
        this.provider = provider;
    }

    @Override
    public void withdraw(Player player, double money) {
        provider.withdrawPlayer(player, money);
    }

    @Override
    public void deposit(Player player, double money) {
        provider.depositPlayer(player, money);
    }

    @Override
    public double get(Player player) {
        return provider.getBalance(player);
    }
}
