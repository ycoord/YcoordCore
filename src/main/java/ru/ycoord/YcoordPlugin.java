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
import ru.ycoord.core.balance.DonateBalance;
import ru.ycoord.core.balance.IBalance;
import ru.ycoord.core.balance.MoneyBalance;
import ru.ycoord.core.color.Color;
import ru.ycoord.core.commands.Command;
import ru.ycoord.core.commands.GuiCommand;
import ru.ycoord.core.messages.ChatMessage;
import ru.ycoord.core.placeholder.IPlaceholderAPI;
import ru.ycoord.core.placeholder.PlaceholderDummy;
import ru.ycoord.core.placeholder.PlaceholderManager;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

public class YcoordPlugin extends JavaPlugin implements EventListener {
    protected IBalance moneyBalance = null;
    protected IBalance donateBalance = null;
    protected PlaceholderManager placeholderManager = null;
    protected List<GuiCommand> guiCommands = new LinkedList<>();
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

        {
            List<Command> roots = getRootCommands();
            if (roots != null) {
                for (Command root : roots)
                    YcoordCore.getInstance().registerCommand(this, root);
            }
        }

        {
            saveAllYmlFiles();
            File dataFolder = getDataFolder().getAbsoluteFile();
            File menusFolder = new File(dataFolder, "menus");
            if (!menusFolder.exists())
                menusFolder.mkdir();
            if (menusFolder.exists()) {
                File[] files = menusFolder.listFiles();
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
    }

    private final Map<String, ConfigurationSection> menus = new HashMap<>();

    public Map<String, ConfigurationSection> getMenus() {
        return menus;
    }

    private void loadMenu(FileConfiguration configuration) {
        String name = configuration.getString("name", null);
        if (name == null)
            return;

        menus.put(name, configuration);
        ConfigurationSection section = configuration.getConfigurationSection("open-command");
        if (section == null)
            return;
        String commandName =  section.getString("name", null);
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

    private void saveAllYmlFiles() {
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

                    saveResource(name, false);
                }
            }
        } catch (Exception ignored) {

        }
    }

    public final ChatMessage getCoreChatMessage(){
        return YcoordCore.getInstance().getChatMessage();
    }

    public List<Command> getRootCommands() {
        return null;
    }

    @Override
    public void onDisable() {
        placeholderManager.unregister();
    }
    public IPlaceholderAPI getPlaceholderAPI() {
        return new PlaceholderDummy();
    }
}
