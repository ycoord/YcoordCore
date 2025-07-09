package ru.ycoord.examples.commands;

import org.apache.logging.log4j.message.SimpleMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.ycoord.YcoordCore;
import ru.ycoord.core.commands.AdminCommand;
import ru.ycoord.core.commands.Command;
import ru.ycoord.core.commands.requirements.Requirement;
import ru.ycoord.core.commands.requirements.SubcommandRequirement;
import ru.ycoord.core.messages.MapMessages;
import ru.ycoord.core.messages.MessagePlaceholders;

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

        @Override
        public List<Requirement> getRequirements(CommandSender sender) {
            return List.of(new SubcommandRequirement(this, List.of(
                    new ExampleMessageCommand()
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
