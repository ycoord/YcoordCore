package ru.ycoord;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.ycoord.core.commands.Command;
import ru.ycoord.core.gui.GuiManager;
import ru.ycoord.core.messages.ChatMessage;
import ru.ycoord.core.messages.MessagePlaceholders;
import ru.ycoord.core.persistance.PlayerDataCache;
import ru.ycoord.core.placeholder.PlaceholderManager;
import ru.ycoord.examples.commands.CoreCommand;

import java.sql.SQLException;
import java.util.*;

public final class YcoordCore extends YcoordPlugin {
    public static YcoordCore instance;
    private ChatMessage chatMessage;
    private PlayerDataCache playerDataCache;
    private GuiManager guiManager;
    private final MessagePlaceholders messagePlaceholders = new MessagePlaceholders(null);
    private final PlaceholderManager placeholderManager = new PlaceholderManager();

    private final Map<String, ConfigurationSection> menus = new HashMap<>();

    public Map<String, ConfigurationSection> getMenus() {
        return menus;
    }
    public static YcoordCore getInstance() {
        return instance;
    }


    public ChatMessage getChatMessage() {
        return chatMessage;
    }

    public PlayerDataCache getPlayerDataCache() {
        return playerDataCache;
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

        placeholderManager.register();


        try {
            playerDataCache = new PlayerDataCache("players.db");

            Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(this, () -> playerDataCache.update(), 20, 10);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        guiManager = new GuiManager();
        guiManager.addGlobalItem(Objects.requireNonNull(getConfig().getConfigurationSection("items.filler")));
        long currentTime = System.currentTimeMillis();
        this.getServer().getPluginManager().registerEvents(guiManager, this);
        try {
            Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(this, () -> guiManager.update(System.currentTimeMillis() - currentTime), 10, 10);
        } catch (Exception e) {
            throw new RuntimeException(e);
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
