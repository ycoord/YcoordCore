package ru.ycoord.core.gui.animation;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.ycoord.core.gui.GuiBase;
import ru.ycoord.core.messages.MessagePlaceholders;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class CornerAnimation extends Animation {
    private final boolean fromCenter;
    public CornerAnimation(ConfigurationSection section) {
        super(section);
        fromCenter =  section.getBoolean("center", false);
    }

    @Override
    protected List<List<Integer>> makeFrames(int w, int h) {
        Map<Integer, List<Integer>> layers = new TreeMap<>();

        int centerY = h / 2;
        int centerX = w / 2;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int index = y * w + x;
                int dist;

                if (fromCenter) {
                    dist = Math.abs(y - centerY) + Math.abs(x - centerX);
                } else {
                    int dTL = y + x;
                    int dTR = y + (w - 1 - x);
                    int dBL = (h - 1 - y) + x;
                    int dBR = (h - 1 - y) + (w - 1 - x);
                    dist = Math.min(Math.min(dTL, dTR), Math.min(dBL, dBR));
                }

                layers.computeIfAbsent(dist, k -> new ArrayList<>()).add(index);
            }
        }

        return new ArrayList<>(layers.values());
    }
}
