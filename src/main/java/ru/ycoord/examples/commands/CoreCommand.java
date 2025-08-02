package ru.ycoord.examples.commands;

import jdk.jshell.execution.Util;
import org.apache.logging.log4j.message.SimpleMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import ru.ycoord.YcoordCore;
import ru.ycoord.core.commands.AdminCommand;
import ru.ycoord.core.commands.Command;
import ru.ycoord.core.commands.requirements.PlayerRequirement;
import ru.ycoord.core.commands.requirements.Requirement;
import ru.ycoord.core.commands.requirements.StringRequirement;
import ru.ycoord.core.commands.requirements.SubcommandRequirement;
import ru.ycoord.core.gui.GuiBase;
import ru.ycoord.core.gui.GuiManager;
import ru.ycoord.core.messages.MapMessages;
import ru.ycoord.core.messages.MessagePlaceholders;
import ru.ycoord.core.nbt.NbtExtension;
import ru.ycoord.core.utils.Utils;

import java.util.List;
import java.util.Map;

public class CoreCommand extends AdminCommand {
    static class ExampleCommand extends AdminCommand {

        static class ExampleMessageCommand extends AdminCommand {

            static class ExampleSimpleMessage extends AdminCommand {

                @Override
                public String getName() {
                    return "simple";
                }

                @Override
                public boolean execute(CommandSender sender, List<String> args, List<Object> params) {
                    if (!super.execute(sender, args, params))
                        return false;


                    if (sender instanceof Player player) {
                        YcoordCore core = YcoordCore.getInstance();
                        core.getChatMessage().sendMessageId(player,
                                "messages.default-message",
                                new MapMessages(player, Map.of(
                                        "%some%", "some value"
                                )));
                    }


                    return true;
                }

                @Override
                public String getDescription(CommandSender sender) {
                    if (sender instanceof Player player) {
                        MessagePlaceholders placeholders = new MessagePlaceholders(player);
                        return messageBase.makeMessageId("messages.example-message-simple-command-description", placeholders);
                    }
                    return "";
                }
            }

            static class ExampleComplexMessage extends AdminCommand {

                @Override
                public String getName() {
                    return "complex";
                }

                @Override
                public boolean execute(CommandSender sender, List<String> args, List<Object> params) {
                    if (!super.execute(sender, args, params))
                        return false;


                    if (sender instanceof Player player) {
                        YcoordCore core = YcoordCore.getInstance();
                        core.getChatMessage().sendMessageId(player,
                                "messages.sound-placeholder-particle-command-hover",
                                new MapMessages(player, Map.of(
                                        "%some%", "some value"
                                )));
                    }


                    return true;
                }

                @Override
                public String getDescription(CommandSender sender) {
                    if (sender instanceof Player player) {
                        MessagePlaceholders placeholders = new MessagePlaceholders(player);
                        return messageBase.makeMessageId("messages.example-message-complex-command-description", placeholders);
                    }
                    return "";
                }
            }

            @Override
            public List<Requirement> getRequirements(CommandSender sender) {
                return List.of(new SubcommandRequirement(this, List.of(
                        new ExampleSimpleMessage(),
                        new ExampleComplexMessage()
                )));
            }

            @Override
            public String getName() {
                return "message";
            }

            @Override
            public String getDescription(CommandSender sender) {
                if (sender instanceof Player player) {
                    MessagePlaceholders placeholders = new MessagePlaceholders(player);
                    return messageBase.makeMessageId("messages.example-message-command-description", placeholders);
                }
                return "";
            }
        }

        static class ExampleNbtCommand extends AdminCommand {
            static class GetNbtCommand extends AdminCommand {
                class BlockCommand extends AdminCommand {
                    @Override
                    public List<Requirement> getRequirements(CommandSender sender) {
                        return List.of(new StringRequirement(this));
                    }

                    @Override
                    public boolean execute(CommandSender sender, List<String> args, List<Object> params) {
                        if (!super.execute(sender, args, params))
                            return false;

                        if (sender instanceof Player player) {
                            Block block = player.getTargetBlock(5);
                            if (block == null) {
                                YcoordCore core = YcoordCore.getInstance();
                                core.getChatMessage().sendMessageId(player, "messages.no-block");
                                return false;
                            }

                            String value = NbtExtension.getString(block.getState(), getParam());

                            YcoordCore core = YcoordCore.getInstance();
                            core.getChatMessage().sendMessageId(player, "messages.block-data", new MapMessages(player, Map.of(
                                    "%value%", value
                            )));
                        }

                        return true;
                    }

                    @Override
                    public String getName() {
                        return "block";
                    }

                    @Override
                    public String getDescription(CommandSender sender) {
                        if (sender instanceof Player player) {
                            MessagePlaceholders placeholders = new MessagePlaceholders(player);
                            return messageBase.makeMessageId("messages.example-get-nbt-block-command-description", placeholders);
                        }
                        return "";
                    }
                }

                class PlayerCommand extends AdminCommand {
                    @Override
                    public List<Requirement> getRequirements(CommandSender sender) {
                        return List.of(new PlayerRequirement(this), new StringRequirement(this));
                    }

                    @Override
                    public boolean execute(CommandSender sender, List<String> args, List<Object> params) {
                        if (!super.execute(sender, args, params))
                            return false;

                        OfflinePlayer targetPlayer = getParam();
                        Bukkit.getOfflinePlayer(targetPlayer.getUniqueId());
                        String key = getParam();

                        YcoordCore core = YcoordCore.getInstance();
                        String value = core.getPlayerDataCache().get(targetPlayer, key);
                        if (value == null) {
                            if (sender instanceof Player player)
                                core.getChatMessage().sendMessageId(player, "messages.no-key");
                        } else if (sender instanceof Player player) {
                            core.getChatMessage().sendMessageId(player, "messages.player-get-data", new MapMessages(player, Map.of(
                                    "%value%", value
                            )));
                        }


                        return true;
                    }

                    @Override
                    public String getName() {
                        return "player";
                    }

                    @Override
                    public String getDescription(CommandSender sender) {
                        if (sender instanceof Player player) {
                            MessagePlaceholders placeholders = new MessagePlaceholders(player);
                            return messageBase.makeMessageId("messages.example-get-nbt-player-command-description", placeholders);
                        }
                        return "";
                    }
                }

                @Override
                public String getName() {
                    return "get";
                }

                @Override
                public String getDescription(CommandSender sender) {
                    if (sender instanceof Player player) {
                        MessagePlaceholders placeholders = new MessagePlaceholders(player);
                        return messageBase.makeMessageId("messages.example-nbt-command-description", placeholders);
                    }
                    return "";
                }

                @Override
                public List<Requirement> getRequirements(CommandSender sender) {
                    return List.of(new SubcommandRequirement(this, List.of(
                            new BlockCommand(),
                            new PlayerCommand()
                    )));
                }
            }

            static class SetNbtCommand extends AdminCommand {

                static class BlockCommand extends AdminCommand {
                    @Override
                    public List<Requirement> getRequirements(CommandSender sender) {
                        return List.of(new StringRequirement(this), new StringRequirement(this));
                    }

                    @Override
                    public boolean execute(CommandSender sender, List<String> args, List<Object> params) {
                        if (!super.execute(sender, args, params))
                            return false;

                        if (sender instanceof Player player) {
                            Block block = player.getTargetBlock(5);
                            if (block == null) {
                                YcoordCore core = YcoordCore.getInstance();
                                core.getChatMessage().sendMessageId(player, "messages.no-block");
                                return false;
                            }

                            String key = getParam();
                            String value = getParam();

                            NbtExtension.setString(block.getState(), key, value);
                            YcoordCore core = YcoordCore.getInstance();
                            core.getChatMessage().sendMessageId(player, "messages.block-set-data", new MapMessages(player, Map.of(
                                    "%key%", key,
                                    "%value%", value
                            )));
                        }

                        return true;
                    }

                    @Override
                    public String getName() {
                        return "block";
                    }

                    @Override
                    public String getDescription(CommandSender sender) {
                        if (sender instanceof Player player) {
                            MessagePlaceholders placeholders = new MessagePlaceholders(player);
                            return messageBase.makeMessageId("messages.example-set-nbt-block-command-description", placeholders);
                        }
                        return "";
                    }
                }

                static class PlayerCommand extends AdminCommand {
                    @Override
                    public List<Requirement> getRequirements(CommandSender sender) {
                        return List.of(
                                new PlayerRequirement(this),
                                new StringRequirement(this), new StringRequirement(this));
                    }

                    @Override
                    public boolean execute(CommandSender sender, List<String> args, List<Object> params) {
                        if (!super.execute(sender, args, params))
                            return false;

                        if (sender instanceof Player player) {
                            OfflinePlayer targetPlayer = getParam();
                            String key = getParam();
                            String value = getParam();

                            YcoordCore core = YcoordCore.getInstance();

                            core.getPlayerDataCache().add(targetPlayer, key, value);

                            core.getChatMessage().sendMessageId(player, "messages.player-set-data", new MapMessages(player, Map.of(
                                    "%key%", key,
                                    "%value%", value
                            )));
                        }

                        return true;
                    }

                    @Override
                    public String getName() {
                        return "player";
                    }

                    @Override
                    public String getDescription(CommandSender sender) {
                        if (sender instanceof Player player) {
                            MessagePlaceholders placeholders = new MessagePlaceholders(player);
                            return messageBase.makeMessageId("messages.example-set-nbt-player-command-description", placeholders);
                        }
                        return "";
                    }
                }


                @Override
                public String getName() {
                    return "set";
                }

                @Override
                public String getDescription(CommandSender sender) {
                    if (sender instanceof Player player) {
                        MessagePlaceholders placeholders = new MessagePlaceholders(player);
                        return messageBase.makeMessageId("messages.example-set-nbt-command-description", placeholders);
                    }
                    return "";
                }

                @Override
                public List<Requirement> getRequirements(CommandSender sender) {
                    return List.of(new SubcommandRequirement(this, List.of(
                            new BlockCommand(),
                            new PlayerCommand()
                    )));
                }
            }

            @Override
            public List<Requirement> getRequirements(CommandSender sender) {
                return List.of(new SubcommandRequirement(this, List.of(
                        new GetNbtCommand(),
                        new SetNbtCommand()
                )));
            }

            @Override
            public String getName() {
                return "nbt";
            }

            @Override
            public String getDescription(CommandSender sender) {
                if (sender instanceof Player player) {
                    MessagePlaceholders placeholders = new MessagePlaceholders(player);
                    return messageBase.makeMessageId("messages.example-nbt-command-description", placeholders);
                }
                return "";
            }
        }

        static class ExamplePlayerHead extends AdminCommand {

            @Override
            public String getName() {
                return "head";
            }

            @Override
            public List<Requirement> getRequirements(CommandSender sender) {
                return List.of(new StringRequirement(this, "nick-completion"));
            }

            @Override
            public String getDescription(CommandSender sender) {
                if (sender instanceof Player player) {
                    MessagePlaceholders placeholders = new MessagePlaceholders(player);
                    return messageBase.makeMessageId("messages.example-head-description", placeholders);
                }
                return "";
            }

            @Override
            public boolean execute(CommandSender sender, List<String> args, List<Object> params) {
                if (!super.execute(sender, args, params))
                    return false;

                String name = getParam();
                if (sender instanceof Player player) {
                    Utils.createPlayerHeadAsync(name).thenAccept(head -> {
                        Bukkit.getScheduler().runTask(YcoordCore.getInstance(), () -> {
                            player.getInventory().addItem(head);
                        });
                    });
                }
                return true;
            }
        }

        static class ExampleGui extends AdminCommand {

            @Override
            public String getName() {
                return "gui";
            }

            @Override
            public String getDescription(CommandSender sender) {
                if (sender instanceof Player player) {
                    MessagePlaceholders placeholders = new MessagePlaceholders(player);
                    return messageBase.makeMessageId("messages.example-gui-description", placeholders);
                }
                return "";
            }

            @Override
            public boolean execute(CommandSender sender, List<String> args, List<Object> params) {
                if (!super.execute(sender, args, params))
                    return false;

                if (sender instanceof Player player) {
                    GuiExample base = new GuiExample(YcoordCore.getInstance().getConfig().getConfigurationSection("gui"));
                    base.open(player);
                }

                return true;
            }
        }

        @Override
        public List<Requirement> getRequirements(CommandSender sender) {
            return List.of(new SubcommandRequirement(this, List.of(
                    new ExampleMessageCommand(),
                    new ExampleNbtCommand(),
                    new ExamplePlayerHead(),
                    new ExampleGui()
            )));
        }

        @Override
        public String getName() {
            return "example";
        }

        @Override
        public String getDescription(CommandSender sender) {
            if (sender instanceof Player player) {
                MessagePlaceholders placeholders = new MessagePlaceholders(player);
                return messageBase.makeMessageId("messages.example-command-description", placeholders);
            }
            return "";
        }
    }


    @Override
    public List<Requirement> getRequirements(CommandSender sender) {
        return List.of(new SubcommandRequirement(this, List.of(
                new ExampleCommand()
        )));
    }

    @Override
    public String getName() {
        return "ycoordcore";
    }

    @Override
    public String getDescription(CommandSender sender) {
        return "";
    }
}
