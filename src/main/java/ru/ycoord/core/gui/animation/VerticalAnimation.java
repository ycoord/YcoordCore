package ru.ycoord.core.gui.animation;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class VerticalAnimation extends Animation{
    private final boolean backward;

    public VerticalAnimation(ConfigurationSection section) {
        super(section);
        this.backward = section.getBoolean("backward", true);
    }

    @Override
    protected List<List<Integer>> makeFrames(int w, int h) {
        List<List<Integer>> frames = new ArrayList<>();

        for (int row = 0; row < h; row++) {
            int y = backward ? (h - 1 - row) : row;
            List<Integer> frame = new ArrayList<>();

            for (int x = 0; x < w; x++) {
                int index = y * w + x;
                frame.add(index);
            }

            frames.add(frame);
        }

        return frames;
    }
}
