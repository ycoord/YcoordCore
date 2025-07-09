package ru.ycoord;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.ycoord.core.commands.Command;
import ru.ycoord.core.persistance.PlayerDataCache;
import ru.ycoord.examples.commands.CoreCommand;
import ru.ycoord.core.messages.ChatMessage;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public final class YcoordCore extends YcoordPlugin {
    public static YcoordCore instance;
    private ChatMessage chatMessage;
    private PlayerDataCache playerDataCache;

    public static YcoordCore getInstance() {
        return instance;
    }


    public ChatMessage getChatMessage() {
        return chatMessage;
    }

    public PlayerDataCache getPlayerDataCache() {
        return playerDataCache;
    }

    @Override
    public void onEnable() {
        instance = this;
        super.onEnable();

        {
            chatMessage = new ChatMessage(this, getConfig());
        }

        try {
            playerDataCache = new PlayerDataCache("players.db");

            Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
                playerDataCache.update();
            }, 20, 10);
        } catch (SQLException e) {
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
