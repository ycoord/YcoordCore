package ru.ycoord.core.gui.animation;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class StepByStep extends Animation {

    private Corner corner;

    public StepByStep(ConfigurationSection section) {
        super(section);
        try {
            this.corner = Corner.valueOf(section.getString("corner"));
        } catch (Exception e) {
            this.corner = Corner.LOWER_LEFT;
        }
    }

    @Override
    protected List<List<Integer>> makeFrames(int w, int h) {
        List<List<Integer>> frames = new ArrayList<>();

        boolean topToBottom = (corner == Corner.UPPER_LEFT || corner == Corner.UPPER_RIGHT);
        boolean leftToRight = (corner == Corner.UPPER_LEFT || corner == Corner.LOWER_LEFT);

        for (int row = 0; row < h; row++) {
            int y = topToBottom ? row : h - 1 - row;

            for (int col = 0; col < w; col++) {
                int x = leftToRight ? col : w - 1 - col;

                int index = y * w + x;
                List<Integer> frame = new ArrayList<>();
                frame.add(index);
                frames.add(frame);
            }
        }

        return frames;
    }
}
