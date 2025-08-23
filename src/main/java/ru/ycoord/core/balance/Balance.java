package ru.ycoord.core.balance;

import org.bukkit.entity.Player;
import ru.ycoord.YcoordCore;

public abstract class Balance {
    public final void withdraw(Player player, double money) {
        subWithdraw(player, money);
        YcoordCore.getInstance().getLogger().info(String.format("Игрок %s снял %f. Тип %s", player.getName(), money, this.getClass().getSimpleName()));
    }

    public final void deposit(Player player, double money) {
        subWithdraw(player, money);
        YcoordCore.getInstance().getLogger().info(String.format("Игроку %s внесено %f. Тип %s", player.getName(), money, this.getClass().getSimpleName()));
    }

    public final double get(Player player) {
        double amount = subGet(player);
        YcoordCore.getInstance().getLogger().info(String.format("Игрок %s извлек свой баланс %f. Тип %s", player.getName(), amount, this.getClass().getSimpleName()));
        return amount;
    }

    abstract void subWithdraw(Player player, double money);

    abstract void subDeposit(Player player, double money);

    abstract double subGet(Player player);
}
