package ru.ycoord;

import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import ru.ycoord.balance.DonateBalance;
import ru.ycoord.balance.IBalance;
import ru.ycoord.balance.MoneyBalance;
import ru.ycoord.color.Color;
import org.black_ixx.playerpoints.PlayerPoints;
import ru.ycoord.messages.ChatMessage;
import ru.ycoord.placeholder.IPlaceholderAPI;
import ru.ycoord.placeholder.PlaceholderDummy;
import ru.ycoord.placeholder.PlaceholderManager;

import java.util.EventListener;
import java.util.logging.Logger;

public class YcoordPlugin extends JavaPlugin implements EventListener {
    protected IBalance moneyBalance = null;
    protected IBalance donateBalance = null;
    protected PlaceholderManager placeholderManager = null;

    boolean requirePlugin(JavaPlugin plugin, String name) {
        Logger logger = plugin.getLogger();

        boolean enabled = Bukkit.getPluginManager().isPluginEnabled(name);
        if (!enabled) {
            logger.severe(String.format("❌ %s is not enabled. Shutting down...", Color.color(Color.YELLOW, name)));
            Bukkit.getPluginManager().disablePlugin(plugin);
            return false;
        }
        logger.info(String.format("✅ %s is enabled.", Color.color(Color.YELLOW, name)));
        return true;
    }

    boolean checkPlugin(JavaPlugin plugin, String name) {
        Logger logger = plugin.getLogger();

        boolean enabled = Bukkit.getPluginManager().isPluginEnabled(name);
        if (!enabled) {
            logger.severe(String.format("❌ %s is not enabled", Color.color(Color.YELLOW, name)));
            return false;
        }
        logger.info(String.format("✅ %s is enabled.", Color.color(Color.YELLOW, name)));
        return true;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        {
            if (!requirePlugin(this, "PlayerPoints"))
                return;

            PlayerPointsAPI pp = PlayerPoints.getInstance().getAPI();
            donateBalance = new DonateBalance(pp);
        }
        {
            if (!requirePlugin(this, "Vault"))
                return;

            RegisteredServiceProvider<Economy> economyProvider = this.getServer().getServicesManager().getRegistration(Economy.class);

            if (economyProvider != null) {
                moneyBalance = new MoneyBalance(economyProvider.getProvider());
            }
        }
        {
            if (!requirePlugin(this, "PlaceholderAPI"))
                return;

            placeholderManager = new PlaceholderManager();
            placeholderManager.register();

            IPlaceholderAPI placeholder = getPlaceholderAPI();
            if (placeholder != null) {
                placeholderManager.registerPlaceholder(placeholder);
            }
        }
    }

    public final ChatMessage getCoreChatMessage(){
        return YcoordCore.getInstance().getChatMessage();
    }

    @Override
    public void onDisable() {
        placeholderManager.unregister();
    }
    public IPlaceholderAPI getPlaceholderAPI() {
        return new PlaceholderDummy();
    }
}
