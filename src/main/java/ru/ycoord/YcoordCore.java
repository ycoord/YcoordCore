package ru.ycoord;

import org.bukkit.command.PluginCommand;
import ru.ycoord.commands.CoreCommand;
import ru.ycoord.messages.ChatMessage;

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

        PluginCommand command = this.getCommand("ycoordcore");
        if (command != null) {
            command.setExecutor(new CoreCommand(this));
        }
    }
}
