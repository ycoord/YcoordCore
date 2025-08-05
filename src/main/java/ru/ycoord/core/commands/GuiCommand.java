package ru.ycoord.core.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import ru.ycoord.YcoordCore;
import ru.ycoord.core.commands.requirements.Requirement;
import ru.ycoord.core.commands.requirements.SubcommandRequirement;
import ru.ycoord.core.gui.GuiBase;
import ru.ycoord.examples.commands.GuiExample;

import java.util.ArrayList;
import java.util.List;

public class GuiCommand extends Command {
    private final ConfigurationSection guiConfig;
    public ConfigurationSection section;
    private List<Requirement> requirements = new ArrayList<Requirement>();

    public GuiCommand(ConfigurationSection guiConfig, ConfigurationSection section) {
        this.section = section;
        this.guiConfig = guiConfig;
        ConfigurationSection requirements = section.getConfigurationSection("requirements");
        if (requirements == null) {
            return;
        }

        for (String key : requirements.getKeys(false)) {
            ConfigurationSection requirement = requirements.getConfigurationSection(key);
            String type = requirement.getString("type", null);
            if (type == null) {
                continue;
            }

            switch (type) {
                case "SUBCOMMAND":
                    this.requirements.add(new SubcommandRequirement(this, List.of(new GuiCommand(guiConfig, requirement))));
                    break;
            }
        }
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args, List<Object> params) {
        if (!super.execute(sender, args, params))
            return false;

        if (sender instanceof Player player) {
            if (!this.requirements.isEmpty()) {
                return true;
            }
            GuiBase base = new GuiBase(guiConfig);
            base.open(player);
        }

        return true;
    }

    @Override
    public String getName() {
        return section.getString("name");
    }

    @Override
    public boolean canExecute(CommandSender sender) {
        return sender.hasPermission(section.getString("permission"));
    }

    @Override
    public String getDescription(CommandSender sender) {
        return section.getString("description", "");
    }

    @Override
    public List<Requirement> getRequirements(CommandSender sender) {
        return requirements;
    }
}
