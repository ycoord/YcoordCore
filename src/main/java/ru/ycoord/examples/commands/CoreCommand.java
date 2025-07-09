package ru.ycoord.examples.commands;

import org.apache.logging.log4j.message.SimpleMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.ycoord.YcoordCore;
import ru.ycoord.core.commands.AdminCommand;
import ru.ycoord.core.commands.Command;
import ru.ycoord.core.commands.requirements.PlayerRequirement;
import ru.ycoord.core.commands.requirements.Requirement;
import ru.ycoord.core.commands.requirements.StringRequirement;
import ru.ycoord.core.commands.requirements.SubcommandRequirement;
import ru.ycoord.core.messages.MapMessages;
import ru.ycoord.core.messages.MessagePlaceholders;
import ru.ycoord.core.nbt.NbtExtension;

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
        }

        static class ExampleNbtCommand extends AdminCommand {
            class GetNbtCommand extends AdminCommand {
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
                }

                @Override
                public String getName() {
                    return "get";
                }

                @Override
                public List<Requirement> getRequirements(CommandSender sender) {
                    return List.of(new SubcommandRequirement(this, List.of(
                            new BlockCommand(),
                            new PlayerCommand()
                    )));
                }
            }

            class SetNbtCommand extends AdminCommand {

                class BlockCommand extends AdminCommand {
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
                }

                class PlayerCommand extends AdminCommand {
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
                }


                @Override
                public String getName() {
                    return "set";
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
        }

        @Override
        public List<Requirement> getRequirements(CommandSender sender) {
            return List.of(new SubcommandRequirement(this, List.of(
                    new ExampleMessageCommand(),
                    new ExampleNbtCommand()
            )));
        }

        @Override
        public String getName() {
            return "example";
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
}
