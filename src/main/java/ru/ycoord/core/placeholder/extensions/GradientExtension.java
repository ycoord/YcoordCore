package ru.ycoord;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import ru.ycoord.core.placeholder.IPlaceholderAPI;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GradientExtension extends IPlaceholderAPI {

    @Override
    public String getId() {
        return "gr";
    }

    @Override
    public String process(OfflinePlayer player, List<String> list) {
        return "";
    }


    public record Result(List<org.bukkit.Color> colors, String cleanedText) {
    }

    public static Result extractColors(String input) {
        Pattern pattern = Pattern.compile("<#([0-9A-Fa-f]{6})>");
        Matcher matcher = pattern.matcher(input);

        List<org.bukkit.Color> colors = new ArrayList<>();
        StringBuilder cleaned = new StringBuilder();

        while (matcher.find()) {
            String hex = matcher.group(1); // только RRGGBB
            Color c = Color.decode("#" + hex);
            colors.add(org.bukkit.Color.fromRGB(c.getRed(), c.getGreen(), c.getBlue()));
            matcher.appendReplacement(cleaned, ""); // удаляем вхождение
        }
        matcher.appendTail(cleaned);

        return new Result(colors, cleaned.toString());
    }

    public static String gradientText(String text, List<org.bukkit.Color> colors, float t, boolean wrap, boolean bold) {
        if (colors.size() < 2) throw new IllegalArgumentException("Минимум два цвета.");
        if (text.isEmpty()) return "";

        final int len = text.length();
        final int n = colors.size();
        StringBuilder sb = new StringBuilder(len * 14);

        // Нормализуем t в [0,1)
        t = t - (float) Math.floor(t);

        // Позиция символа по строке:
        // для незамкнутого — [0,1] (чтобы последний символ точно был последним цветом)
        // для замкнутого — [0,1) (чтобы не было дубля первой точки)
        for (int i = 0; i < len; i++) {
            float u = (len == 1) ? 0f : (wrap ? (i / (float) len) : (i / (float) (len - 1)));

            float raw;   // позиция в "цветовом пространстве"
            int seg;     // индекс сегмента
            float local; // локальная доля внутри сегмента

            if (wrap) {
                // Замкнутый режим: спокойно модим по числу цветов
                float phase = t * n;
                raw = (u * n + phase) % n;
                seg = (int) Math.floor(raw);
                local = raw - seg;

                org.bukkit.Color a = colors.get(seg);
                org.bukkit.Color b = colors.get((seg + 1) % n);

                doWork(text, bold, sb, i, local, a, b);
            } else {
                // НЕзамкнутый режим: пинг-понг по t, без модулей по цветам
                // triangle(t): 0→1→0… даёт плавный сдвиг без скачков на границах
                float tri = 1f - Math.abs(2f * t - 1f); // в диапазоне [0,1]
                // Сдвигаем по строке: u' = clamp(u + shift, 0, 1)
                // Чем больше tri, тем дальше "окно" градиента сдвинуто.
                // Масштаб сдвига можно настроить коэффициентом; возьмём полный диапазон:
                float shifted = clamp01(u + tri);

                // Преобразуем в пространство сегментов [0, n-1] линейно (без замыкания)
                float pos = shifted * (n - 1); // [0 .. n-1]
                seg = (int) Math.floor(pos);
                if (seg >= n - 1) {
                    seg = n - 2;
                    pos = n - 1;
                } // край
                local = pos - seg;

                org.bukkit.Color a = colors.get(seg);
                org.bukkit.Color b = colors.get(seg + 1);

                doWork(text, bold, sb, i, local, a, b);
            }
        }

        return sb.toString();
    }

    private static void doWork(String text, boolean bold, StringBuilder sb, int i, float local, org.bukkit.Color a, org.bukkit.Color b) {
        int r = Math.round(a.getRed() + local * (b.getRed() - a.getRed()));
        int g = Math.round(a.getGreen() + local * (b.getGreen() - a.getGreen()));
        int bch = Math.round(a.getBlue() + local * (b.getBlue() - a.getBlue()));

        String hex = String.format("#%02X%02X%02X", r, g, bch);
        if (bold)
            sb.append(ChatColor.of(hex)).append("§l").append(text.charAt(i));
        else
            sb.append(ChatColor.of(hex)).append(text.charAt(i));
    }

    private static float clamp01(float x) {
        return (x < 0f) ? 0f : (Math.min(x, 1f));
    }

    @Override
    public String processString(OfflinePlayer player, String identifier) {
        String[] data = identifier.split("_", 2);
        if (data.length == 2) {
            String v = data[1];
            Result r = extractColors(v);

            String cleanedText = r.cleanedText;

            boolean time = cleanedText.startsWith("~");
            int duration = 5000;
            if (time) {
                cleanedText = cleanedText.substring(1);
                StringBuilder num = new StringBuilder();
                for (char c : cleanedText.toCharArray()) {
                    if (Character.isDigit(c)) {
                        num.append(c);
                        cleanedText = cleanedText.substring(1);
                    } else {
                        break;
                    }
                }
                if (!num.isEmpty())
                    duration = Integer.parseInt(num.toString());
            }

            boolean bold = cleanedText.startsWith("*");

            if (bold) {
                cleanedText = cleanedText.substring(1);
            }


            if (cleanedText.startsWith("{") && cleanedText.endsWith("}")) {
                String s = cleanedText.substring(1, cleanedText.length() - 1);
                String placeholder = "%" + s + "%";
                String result = PlaceholderAPI.setPlaceholders(player, placeholder);
                if (result.equalsIgnoreCase(placeholder)) {
                    cleanedText = s;
                } else {
                    cleanedText = result;
                }
            }

            float currentTime = (System.currentTimeMillis() % duration) / (float) duration;

            return gradientText(cleanedText, r.colors, time ? currentTime : 0, time, bold);
        }
        return "";
    }
}
