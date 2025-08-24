package ru.ycoord.core.placeholder.extensions;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import ru.ycoord.YcoordCore;
import ru.ycoord.core.messages.MessageBase;
import ru.ycoord.core.messages.MessagePlaceholders;
import ru.ycoord.core.placeholder.IPlaceholderAPI;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InnerExtension extends IPlaceholderAPI {

    public interface PlaceholderProcessor {
        String processPlaceholder(String placeholder);
    }

    public static String resolve(String input, PlaceholderProcessor processor) {
        if (input == null || input.isEmpty()) return input;

        String prev;
        String cur = input;

        // Повторяем проходы, пока есть изменения и потенциальные плейсхолдеры
        for (int safety = 0; safety < 1000 && cur.indexOf('{') >= 0; safety++) {
            prev = cur;
            cur = resolveOnePass(cur, processor);
            if (cur.equals(prev)) break; // больше нечего менять
        }
        return cur;
    }

    private static String resolveOnePass(String s, PlaceholderProcessor processor) {
        Deque<StringBuilder> stack = new ArrayDeque<>();
        stack.push(new StringBuilder()); // корневой буфер (вне любых скобок)

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == '{') {
                // начинаем новый внутренний плейсхолдер
                stack.push(new StringBuilder());
            } else if (c == '}') {
                if (stack.size() > 1) {
                    // закрываем самый внутренний плейсхолдер
                    String inner = stack.pop().toString();
                    String replacement = processor.processPlaceholder(inner);
                    if (replacement == null) {
                        // не знаем подстановку — оставляем как есть
                        replacement = "{" + inner + "}";
                    }
                    // добавляем результат в внешний уровень
                    stack.peek().append(replacement);
                } else {
                    // лишняя закрывающая скобка — трактуем как литерал
                    stack.peek().append('}');
                }
            } else {
                stack.peek().append(c);
            }
        }

        // Если остались незакрытые '{...'
        while (stack.size() > 1) {
            String dangling = stack.pop().toString();
            stack.peek().append('{').append(dangling);
        }

        return stack.peek().toString();
    }

    @Override
    public String getId() {
        return "ie";
    }

    @Override
    public String process(OfflinePlayer player, List<String> list) {
        return "";
    }

    @Override
    public String processString(OfflinePlayer player, String identifier) {
        String[] data = identifier.split("_", 2);
        if (data.length == 2) {
            return resolve(data[1], placeholder -> {
                String builder = "%" + placeholder + "%";

                MessagePlaceholders messagePlaceholders = YcoordCore.getInstance().getGlobalPlaceholders();
                builder = messagePlaceholders.apply(MessageBase.Level.NONE, builder);

                String replaced = PlaceholderAPI.setPlaceholders(player, builder);

                if(replaced.equals(builder)){
                    return builder;
                }

                return replaced;
            });
        }
        return "";
    }
}
