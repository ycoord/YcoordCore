package ru.ycoord.core.gui.animation;

import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class DiagonalAnimation extends Animation{
    private Corner corner;

    public DiagonalAnimation(ConfigurationSection section) {
        super(section);
        try {
            this.corner = Corner.valueOf(section.getString("corner"));
        } catch (Exception e) {
            this.corner = Corner.LOWER_RIGHT;
        }
    }
    @Override
    protected List<List<Integer>> makeFrames(int w, int h) {
        Map<Integer, List<Integer>> diagonals = new TreeMap<>();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int index = y * w + x;
                int key;

                switch (corner) {
                    case UPPER_LEFT:
                    case LOWER_RIGHT:
                        key = x + y;
                        break;
                    case UPPER_RIGHT:
                    case LOWER_LEFT:
                        key = y - x;
                        break;
                    default:
                        key = x + y;
                }

                diagonals.computeIfAbsent(key, k -> new ArrayList<>()).add(index);
            }
        }

        List<List<Integer>> result = new ArrayList<>(diagonals.values());

        // Для "нижних" углов — порядок диагоналей обратный
        if (corner == Corner.LOWER_LEFT || corner == Corner.LOWER_RIGHT) {
            Collections.reverse(result);
        }

        return result;
    }
}
