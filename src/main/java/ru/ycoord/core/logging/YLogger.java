package ru.ycoord.core.logging;


import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class YLogger {
    private final JavaPlugin plugin;

    protected YLogger(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public enum Level {
        INFO,
        ERROR,
        FATAL,
        PANIC
    }

    abstract void log(Level level, String message);

    public void info(String message) {
        log(Level.INFO, message);
    }

    public void error(String message) {
        log(Level.ERROR, message);
    }

    public void fatal(String message) {
        log(Level.FATAL, message);
        Bukkit.getPluginManager().disablePlugin(plugin);
    }

    public void panic(String message) {
        log(Level.PANIC, message);
        Bukkit.shutdown();
    }
}
