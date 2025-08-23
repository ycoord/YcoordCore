package ru.ycoord.core.balance;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import ru.ycoord.YcoordCore;

public abstract class Balance {
    public ConfigurationSection config;

    public Balance(ConfigurationSection config) {
        this.config = config;
    }

    public final void withdraw(Player player, int money) {
        subWithdraw(player, money);
        YcoordCore.getInstance().getLogger().info(String.format("Игрок %s снял %f. Тип %s", player.getName(), money, this.getClass().getSimpleName()));
    }

    public final void deposit(Player player, int money) {
        subWithdraw(player, money);
        YcoordCore.getInstance().getLogger().info(String.format("Игроку %s внесено %f. Тип %s", player.getName(), money, this.getClass().getSimpleName()));
    }

    public final int get(Player player) {
        int amount = subGet(player);
        YcoordCore.getInstance().getLogger().info(String.format("Игрок %s извлек свой баланс %f. Тип %s", player.getName(), amount, this.getClass().getSimpleName()));
        return amount;
    }

    public String format(int amount) {
        if (config == null)
            return String.valueOf(amount);

        String style = config.getString("style", null);
        if (style == null)
            return String.valueOf(amount);

        return style.replace("%AMOUNT%", String.valueOf(amount));
    }

    abstract void subWithdraw(Player player, int money);

    abstract void subDeposit(Player player, int money);

    abstract int subGet(Player player);
}
