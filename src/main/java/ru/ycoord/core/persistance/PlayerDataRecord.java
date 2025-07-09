package ru.ycoord.core.persistance;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.UUID;

@DatabaseTable(tableName = "player_data")
public class PlayerDataRecord {
    @DatabaseField(id = true)
    private String id; // uuid:key

    @DatabaseField
    private String uuid;

    @DatabaseField
    private String key;

    @DatabaseField
    private String value;

    public PlayerDataRecord() {}

    public PlayerDataRecord(String uuid, String key, String value) {
        this.id = uuid.toString() + ":" + key;
        this.uuid = uuid.toString();
        this.key = key;
        this.value = value;
    }

    public String getId() { return id; }
    public String getUuid() { return uuid; }
    public String getKey() { return key; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}
