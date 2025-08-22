package ru.ycoord.core.persistance;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;
import ru.ycoord.YcoordCore;

import java.io.File;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;

public class PlayerDataCache {

    private final Dao<PlayerDataRecord, String> dao;
    // Весь кэш: UUID -> (key -> value)
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, String>> cache = new ConcurrentHashMap<>();
    // UUID:key "грязные" данные, которые надо сбросить в БД
    private final Set<String> dirtySet = ConcurrentHashMap.newKeySet();

    public PlayerDataCache(String dbPath) throws SQLException {
        String path = YcoordCore.getInstance().getDataFolder() + File.separator + dbPath;
        String url = "jdbc:sqlite:" + path;

        JdbcConnectionSource connectionSource = new JdbcConnectionSource(url);
        dao = com.j256.ormlite.dao.DaoManager.createDao(connectionSource, PlayerDataRecord.class);
        TableUtils.createTableIfNotExists(connectionSource, PlayerDataRecord.class);
        loadAll(); // загрузить всё при старте
    }

    // Загрузка всех данных из базы в память
    private void loadAll() throws SQLException {
        for (PlayerDataRecord record : dao.queryForAll()) {
            String uuid = record.getUuid();
            cache
                    .computeIfAbsent(uuid, k -> new ConcurrentHashMap<>())
                    .put(record.getKey(), record.getValue());
        }
    }

    // Добавить/обновить значение в кэше (пометить как dirty)
    public void add(OfflinePlayer player, String key, String value) {
        String uuid = player.getName();
        cache
                .computeIfAbsent(uuid, k -> new ConcurrentHashMap<>())
                .put(key, value);
        dirtySet.add(uuid + ":" + key);
    }

    // Получить значение (null если нет)
    public @Nullable String get(OfflinePlayer player, String key) {
        String uuid = player.getName();
        Map<String, String> map = cache.get(uuid);
        if (map == null) return null;
        return map.get(key);
    }

    public boolean has(OfflinePlayer player, String key) {
        String uuid = player.getName();
        Map<String, String> map = cache.get(uuid);
        if(map == null) return false;
        return  map.containsKey(key);
    }


    // Сбросить dirty-данные в БД
    public void update() {
        Set<String> dirtyCopy = new HashSet<>(dirtySet);
        dirtySet.removeAll(dirtyCopy); // Очищаем помеченные dirty
        for (String id : dirtyCopy) {
            String[] parts = id.split(":", 2);
            if (parts.length != 2) continue;
            String uuid = parts[0];
            String key = parts[1];
            String value = cache.getOrDefault(uuid, new ConcurrentHashMap<>()).get(key);

            try {
                PlayerDataRecord record = new PlayerDataRecord(uuid, key, value);
                dao.createOrUpdate(record);
            } catch (SQLException e) {
                e.printStackTrace();
                // не забываем вернуть в dirtySet если не удалось сохранить!
                dirtySet.add(id);
            }
        }
    }
}
