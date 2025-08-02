package ru.ycoord.core.gui.animation;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class HorizontalAnimation extends Animation{
    private final boolean backward;

    public HorizontalAnimation(ConfigurationSection section) {
        super(section);
        this.backward = section.getBoolean("backward", true);
    }

    @Override
    protected List<List<Integer>> makeFrames(int w, int h) {
        List<List<Integer>> frames = new ArrayList<>();

        for (int col = 0; col < w; col++) {
            int x = backward ? (w - 1 - col) : col;
            List<Integer> frame = new ArrayList<>();

            for (int y = 0; y < h; y++) {
                int index = y * w + x;
                frame.add(index);
            }

            frames.add(frame);
        }

        return frames;
    }
}
