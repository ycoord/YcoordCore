package ru.ycoord;

import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.ycoord.core.balance.*;
import ru.ycoord.core.color.Color;
import ru.ycoord.core.commands.Command;
import ru.ycoord.core.commands.GuiCommand;
import ru.ycoord.core.logging.FileLogger;
import ru.ycoord.core.logging.YLogger;
import ru.ycoord.core.messages.ChatMessage;
import ru.ycoord.core.messages.MessagePlaceholders;
import ru.ycoord.core.placeholder.IPlaceholderAPI;
import ru.ycoord.core.placeholder.PlaceholderDummy;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

public class YcoordPlugin extends JavaPlugin implements EventListener {
    protected List<GuiCommand> guiCommands = new LinkedList<>();
    private YLogger logger;

    public YLogger logger() {
        return logger;
    }

    public boolean doesntRequirePlugin(JavaPlugin plugin, String name) {
        Logger logger = plugin.getLogger();

        boolean enabled = Bukkit.getPluginManager().isPluginEnabled(name);
        if (!enabled) {
            logger.severe(String.format("❌ %s is not enabled. Shutting down...", Color.color(Color.YELLOW, name)));
            Bukkit.getPluginManager().disablePlugin(plugin);
            return true;
        }
        logger.info(String.format("✅ %s is enabled.", Color.color(Color.YELLOW, name)));
        return false;
    }

    public boolean checkPlugin(JavaPlugin plugin, String name) {
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
        boolean replace = true;
        @NotNull FileConfiguration cfg = getConfig();
        this.saveResource("config.yml", replace);

        logger = new FileLogger(getDataFolder(), this);

        {
            if (doesntRequirePlugin(this, "PlaceholderAPI"))
                return;


            IPlaceholderAPI placeholder = getPlaceholderAPI();
            if (placeholder != null) {
                YcoordCore.getInstance().getPlaceholderManager().registerPlaceholder(placeholder);
            }
        }

        {
            List<Command> roots = getRootCommands();
            if (roots != null) {
                for (Command root : roots)
                    YcoordCore.getInstance().registerCommand(this, root);
            }
        }

        {
            saveAllYmlFiles(replace);
            File dataFolder = getDataFolder().getAbsoluteFile();
            File menusFolder = new File(dataFolder, "menus");
            if (!menusFolder.exists()) {
                boolean ignored = menusFolder.mkdir();
            }
            if (menusFolder.exists()) {
                File[] files = menusFolder.listFiles();
                assert files != null;
                for (File file : files) {
                    if (!file.isFile())
                        continue;

                    FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                    loadMenu(config);
                }
            }


            for (Command root : guiCommands)
                YcoordCore.getInstance().registerCommand(this, root);
        }

        {
            MessagePlaceholders messagePlaceholders = YcoordCore.getInstance().getGlobalPlaceholders();
            ConfigurationSection s = getConfig();
            ConfigurationSection placeholders = s.getConfigurationSection("placeholders");
            if (placeholders != null) {
                for (String key : placeholders.getKeys(false)) {
                    ConfigurationSection placeholderData = placeholders.getConfigurationSection(key);
                    if (placeholderData != null) {

                        String placeholderKey = placeholderData.getString("placeholder");
                        String placeholderValue = placeholderData.getString("result");

                        assert placeholderKey != null;
                        messagePlaceholders.put(placeholderKey, placeholderValue);
                    }
                }
            }
        }

        {
            ChatMessage chatMessage = YcoordCore.getInstance().getChatMessage();
            chatMessage.addConfig(getConfig());
        }
    }


    private void loadMenu(FileConfiguration configuration) {
        String name = configuration.getString("name", null);
        if (name == null)
            return;

        YcoordCore.getInstance().getMenus().put(name, configuration);
        ConfigurationSection section = configuration.getConfigurationSection("open-command");
        if (section == null)
            return;
        String commandName = section.getString("name", null);
        if (commandName == null)
            return;
        boolean merged = false;
        for (GuiCommand guiCommand : guiCommands) {
            if (guiCommand.getName().equalsIgnoreCase(commandName)) {
                guiCommand.merge(new GuiCommand(configuration, section));
                merged = true;
                break;
            }
        }
        if (!merged)
            guiCommands.add(new GuiCommand(configuration, section));
    }

    private void saveAllYmlFiles(boolean replace) {
        try {
            URL url = getClassLoader().getResource("menus");

            assert url != null;
            JarURLConnection jarConnection = (JarURLConnection) url.openConnection();
            JarFile jarFile = jarConnection.getJarFile();
            Enumeration<JarEntry> entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name.endsWith(".yml") && !entry.isDirectory()) {
                    if (name.equalsIgnoreCase("config.yml") || name.equalsIgnoreCase("plugin.yml"))
                        continue;

                    saveResource(name, replace);
                }
            }
        } catch (Exception ignored) {

        }
    }


    public List<Command> getRootCommands() {
        return null;
    }

    @Override
    public void onDisable() {
        //YcoordCore.getInstance().getPlaceholderManager().unregister();
    }

    public IPlaceholderAPI getPlaceholderAPI() {
        return new PlaceholderDummy();
    }
}
