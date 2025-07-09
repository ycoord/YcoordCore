package ru.ycoord.sound;

import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import static org.bukkit.SoundCategory.AMBIENT;

public class SoundInfo {
    private final Sound sound;
    private final float pitch;
    private final float volume;
    private final SoundCategory category;

    public SoundInfo(ConfigurationSection section) {
        this.sound = Sound.valueOf(section.getString("name", "ENTITY_VILLAGER_NO"));
        this.category = SoundCategory.valueOf(section.getString("category", "AMBIENT"));
        this.pitch = (float) section.getDouble("pitch", 1);
        this.volume = (float) section.getDouble("volume", 1);
    }

    public SoundInfo(Sound sound, SoundCategory cat, float volume, float pitch) {
        this.sound = sound;

        this.category = cat;

        this.pitch = pitch;
        this.volume = volume;
    }


    public void play(OfflinePlayer player) {
        if(player.isOnline()){
            @Nullable Player p = player.getPlayer();
            assert p != null;
            p.playSound(p.getLocation(), sound, category, volume, pitch);
        }
    }
}