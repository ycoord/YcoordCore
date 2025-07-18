package ru.ycoord.core.placeholder;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import ru.ycoord.YcoordCore;

import java.util.List;

public class CorePlaceholders implements IPlaceholderAPI {
    @Override
    public String getId() {
        return "ycoordcore";
    }

    @Override
    public String process(Player player, List<String> args) {
        int len = args.size();
        YcoordCore core = YcoordCore.getInstance();

        switch (len) {
            case 0, 1, 2:
                return null;
            case 3:
            {
                if(args.get(0).equalsIgnoreCase("player")) {
                    String target = args.get(1);
                    OfflinePlayer targetPlayer = Bukkit.getOfflinePlayerIfCached(target);
                    if(targetPlayer == null)
                        return null;
                    String key = args.get(2);
                    return core.getPlayerDataCache().get(targetPlayer, key);
                }
                break;
            }
        }

        return "";
    }
}
