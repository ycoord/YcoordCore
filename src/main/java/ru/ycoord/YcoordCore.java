package ru.ycoord;

import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.ycoord.core.balance.*;
import ru.ycoord.core.commands.Command;
import ru.ycoord.core.gui.GuiManager;
import ru.ycoord.core.messages.ChatMessage;
import ru.ycoord.core.messages.MessagePlaceholders;
import ru.ycoord.core.persistance.PlayerDataCache;
import ru.ycoord.core.placeholder.PlaceholderManager;
import ru.ycoord.examples.commands.CoreCommand;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public final class YcoordCore extends YcoordPlugin {
    public static YcoordCore instance;
    private ChatMessage chatMessage;
    private GuiManager guiManager;
    private final MessagePlaceholders messagePlaceholders = new MessagePlaceholders(null);
    private final PlaceholderManager placeholderManager = new PlaceholderManager();

    private final Map<String, ConfigurationSection> menus = new HashMap<>();
    private ConcurrentHashMap<String, Balance> balances = new ConcurrentHashMap<>();

    public Balance getMoneyBalance() {
        return balances.get("MONEY");
    }

    public Balance getDonateBalance() {
        return balances.get("MONEY");
    }

    public Balance getExpBalance() {
        return balances.get("EXP");
    }

    public Balance getLevelBalance() {
        return balances.get("LEVEL");
    }

    public @Nullable Balance getBalance(String balance) {
        return balances.getOrDefault(balance, null);
    }

    public Map<String, ConfigurationSection> getMenus() {
        return menus;
    }

    public static YcoordCore getInstance() {
        return instance;
    }


    public ChatMessage getChatMessage() {
        return chatMessage;
    }


    public GuiManager getGuiManager() {
        return guiManager;
    }

    public MessagePlaceholders getGlobalPlaceholders() {
        return messagePlaceholders;
    }

    public PlaceholderManager getPlaceholderManager() {
        return placeholderManager;
    }

    @Override
    public void onEnable() {
        instance = this;

        {
            chatMessage = new ChatMessage(this, new YamlConfiguration());
        }

        super.onEnable();

        ConfigurationSection moneySection = getConfig().getConfigurationSection("money");


        if (doesntRequirePlugin(this, "PlayerPoints"))
            return;

        PlayerPointsAPI pp = PlayerPoints.getInstance().getAPI();


        if (doesntRequirePlugin(this, "Vault"))
            return;

        RegisteredServiceProvider<Economy> economyProvider = this.getServer().getServicesManager().getRegistration(Economy.class);

        assert moneySection != null;
        for (String key : moneySection.getKeys(false)) {
            ConfigurationSection moneyCfg = moneySection.getConfigurationSection(key);
            assert moneyCfg != null;
            String type = moneyCfg.getString("type", null);
            if (type == null)
                continue;

            if (type.equalsIgnoreCase("MONEY")) {
                assert economyProvider != null;
                balances.put(type, new MoneyBalance(moneyCfg, economyProvider.getProvider()));
            } else if (type.equalsIgnoreCase("DONATE")) {
                balances.put(type, new DonateBalance(moneyCfg, pp));
            } else if (type.equalsIgnoreCase("EXP")) {
                balances.put(type, new ExpBalance(moneyCfg));
            } else if (type.equalsIgnoreCase("LEVEL")) {
                balances.put(type, new LevelBalance(moneyCfg));
            } else {
                balances.put(type, new CustomBalance(moneyCfg));
            }
        }


        placeholderManager.register();

        guiManager = new GuiManager();


        ConfigurationSection s = getConfig().getConfigurationSection("items");
        assert s != null;
        for (String key : s.getKeys(false)) {
            ConfigurationSection itemCfg = s.getConfigurationSection(key);
            guiManager.addGlobalItem(Objects.requireNonNull(itemCfg));
        }

        long currentTime = System.currentTimeMillis();
        this.getServer().getPluginManager().registerEvents(guiManager, this);
        try {
            Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(this, () -> guiManager.update(System.currentTimeMillis() - currentTime), 1, 1);
        } catch (Exception e) {
            YcoordCore.getInstance().logger().error(e.getMessage());
        }
    }

    @Override
    public List<Command> getRootCommands() {
        return List.of(new CoreCommand());
    }


    static class Handler implements CommandExecutor, TabCompleter {

        private final Command base;

        public Handler(Command base) {
            this.base = base;
        }

        @Override
        public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String alias, @NotNull String[] args) {
            return base.complete(sender, List.of(args));
        }


        @Override
        public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args) {
            return base.execute(sender, List.of(args), new LinkedList<>());
        }
    }

    public void registerCommand(JavaPlugin plugin, Command command) {
        Handler handler = new Handler(command);
        PluginCommand cmd = plugin.getCommand(command.getName());
        if (cmd == null)
            return;
        cmd.setExecutor(handler);
        cmd.setTabCompleter(handler);
    }
}
