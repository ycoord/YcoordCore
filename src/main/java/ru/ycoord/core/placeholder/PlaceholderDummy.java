package ru.ycoord.core.placeholder;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;

import java.util.List;

public class PlaceholderDummy implements IPlaceholderAPI {
    @Override
    public String getId() {
        return "dummy";
    }


    public static String gradientText(String text, List<Color> colors, float t) {
        if (colors.size() < 2) throw new IllegalArgumentException("Минимум два цвета для градиента.");
        if (text.length() == 0) return "";

        StringBuilder builder = new StringBuilder();
        int textLength = text.length();
        int segments = colors.size() - 1;

        // Нормализуем t
        t = t % 1.0f;
        if (t < 0) t += 1.0f;

        for (int i = 0; i < textLength; i++) {
            float globalRatio = (float) i / (textLength - 1);
            globalRatio = (globalRatio + t) % 1.0f;

            int segment = Math.min((int) (globalRatio * segments), segments - 1);
            float localRatio = (globalRatio - ((float) segment / segments)) * segments;

            Color start = colors.get(segment);
            Color end = colors.get(segment + 1);

            int red = (int) (start.getRed() + localRatio * (end.getRed() - start.getRed()));
            int green = (int) (start.getGreen() + localRatio * (end.getGreen() - start.getGreen()));
            int blue = (int) (start.getBlue() + localRatio * (end.getBlue() - start.getBlue()));

            Color currentColor = Color.fromRGB(red, green, blue);
            String hex = String.format("#%02x%02x%02x", currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue());

            builder.append(ChatColor.of(hex)).append(text.charAt(i));
        }

        return builder.toString();
    }

    @Override
    public String process(Player player, List<String> args) {

        float time = (System.currentTimeMillis() % 5000L) / 5000f;
        return gradientText("helloworldfsdf23f2", List.of(Color.AQUA, Color.FUCHSIA, Color.WHITE, Color.YELLOW, Color.AQUA), time);
    }
}
