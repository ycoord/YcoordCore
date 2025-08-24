package ru.ycoord.core.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import ru.ycoord.YcoordCore;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class ConfigurationUtil
{

    public static FileConfiguration loadConfiguration(Plugin plugin, File dataFolder, String fileName)
    {
        File file = new File(dataFolder, fileName);
        if (!file.exists()) {
            plugin.saveResource(fileName, false);

        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration loadConfiguration(Plugin plugin, String fileName) {
        return loadConfiguration(plugin, plugin.getDataFolder(), fileName);
    }

    public static void loadConfigurations(Plugin plugin, String... fileNames) {
        Arrays.stream(fileNames).forEach(name -> loadConfiguration(plugin, name));
    }


    public static File createFile(File dataFolder, String fileName) {
        File file  = new File(dataFolder, fileName);
        if (!file.exists()) {
            file.mkdir();
        }
        return file;
    }

    public static File createFile(Plugin plugin, String fileName) {
        return createFile(plugin.getDataFolder(), fileName);
    }

    public static void saveFile(FileConfiguration fileConfiguration, String dataFolder, String fileName) {
        try {
            fileConfiguration.save(new File(dataFolder, fileName));
        } catch (IOException e) {
            YcoordCore.getInstance().logger().error(e.getMessage());
        }
    }

    public static FileConfiguration reloadFile(Plugin plugin, String fileName) {
        return loadConfiguration(plugin, fileName);
    }
}
