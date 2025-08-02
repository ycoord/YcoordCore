package ru.ycoord.core.gui.animation;

import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class SpiralAnimation extends Animation {

    private final boolean backward;
    private Corner corner;

    public SpiralAnimation(ConfigurationSection section) {
        super(section);
        this.backward = section.getBoolean("backward", false);
        try {
            this.corner = Corner.valueOf(section.getString("corner"));
        } catch (Exception e) {
            this.corner = Corner.LOWER_LEFT;
        }
    }

    @Override
    protected List<List<Integer>> makeFrames(int w, int h) {
        boolean[][] visited = new boolean[h][w];
        List<List<Integer>> frames = new ArrayList<>();

        int[][] clockwise = { {1, 0}, {0, 1}, {-1, 0}, {0, -1} };          // → ↓ ← ↑
        int[][] counterClockwise = { {0, 1}, {1, 0}, {0, -1}, {-1, 0} };   // ↓ → ↑ ←
        int[][] dirs = backward ? counterClockwise : clockwise;

        int dir = 0;
        int x = 0, y = 0;

        // начальные координаты и направление
        switch (corner) {
            case UPPER_LEFT:
                x = 0; y = 0; dir = 0; break;
            case UPPER_RIGHT:
                x = w - 1; y = 0; dir = backward ? 1 : 3; break;
            case LOWER_RIGHT:
                x = w - 1; y = h - 1; dir = backward ? 2 : 1; break;
            case LOWER_LEFT:
                x = 0; y = h - 1; dir = backward ? 3 : 2; break;
        }

        for (int step = 0; step < w * h; step++) {
            visited[y][x] = true;
            frames.add(List.of(y * w + x));

            int nx = x + dirs[dir][0];
            int ny = y + dirs[dir][1];

            if (nx < 0 || nx >= w || ny < 0 || ny >= h || visited[ny][nx]) {
                // поворот по часовой или против
                dir = (dir + 1) % 4;
                nx = x + dirs[dir][0];
                ny = y + dirs[dir][1];
            }

            x = nx;
            y = ny;
        }

        return frames;
    }
}
