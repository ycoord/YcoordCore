package ru.ycoord;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.ycoord.core.commands.Command;
import ru.ycoord.examples.commands.CoreCommand;
import ru.ycoord.core.messages.ChatMessage;

import java.util.LinkedList;
import java.util.List;

public final class YcoordCore extends YcoordPlugin {
    public static YcoordCore instance;
    private ChatMessage chatMessage;

    public static YcoordCore getInstance() {
        return instance;
    }


    public ChatMessage getChatMessage() {
        return chatMessage;
    }

    @Override
    public void onEnable() {
        instance = this;
        super.onEnable();

        {
            chatMessage = new ChatMessage(this, getConfig());
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
        if(cmd == null)
            return;
        cmd.setExecutor(handler);
        cmd.setTabCompleter(handler);
    }
}
