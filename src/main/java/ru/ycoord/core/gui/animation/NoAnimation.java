package ru.ycoord.core.gui.animation;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import ru.ycoord.core.gui.GuiBase;
import ru.ycoord.core.messages.MessagePlaceholders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NoAnimation extends Animation {
    public NoAnimation(@Nullable ConfigurationSection section) {
        super(section);
    }


    @Override
    protected List<List<Integer>> makeFrames(int w, int h) {
        List<List<Integer>> frames = new ArrayList<>();
        List<Integer> frame = new ArrayList<>();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int index = makeIndex(x, y, w);
                frame.add(index);
            }
        }
        frames.add(frame);
        return frames;
    }
}
