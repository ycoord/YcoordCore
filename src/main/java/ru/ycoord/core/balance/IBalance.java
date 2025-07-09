package ru.ycoord.core.balance;

import org.bukkit.entity.Player;

public interface IBalance {
    void withdraw(Player player, double money);
    void deposit(Player player, double money);
    double get(Player player);
}
