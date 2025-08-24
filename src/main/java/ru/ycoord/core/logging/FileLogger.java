package ru.ycoord.core.logging;

import org.bukkit.plugin.java.JavaPlugin;
import ru.ycoord.YcoordCore;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileLogger extends YLogger {

    private final File logFile;

    public FileLogger(File dataFolder, JavaPlugin plugin) {
        super(plugin);
        if (!dataFolder.exists()) dataFolder.mkdirs();

        String timestamp = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss").format(new Date());
        this.logFile = new File(dataFolder, "log_" + timestamp + ".txt");
    }


    @Override
    public void log(Level level, String message) {
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        try (FileWriter fw = new FileWriter(logFile, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println("[" + time + "] " + "<" + level.name() + "> " + message);
        } catch (IOException e) {
            YcoordCore.getInstance().logger().error(e.getMessage());
        }
    }
}
