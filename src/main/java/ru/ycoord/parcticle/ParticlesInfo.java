package ru.ycoord.parcticle;

import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

public class ParticlesInfo {
    private final int count;
    private final float x;
    private final float y;
    private final float z;
    private final Particle particle;

    public ParticlesInfo(ConfigurationSection section) {
        this.particle = Particle.valueOf(section.getString("name", "EXPLOSION_NORMAL"));
        this.count = section.getInt("count", 100);
        this.x = section.getInt("offset.x", 0);
        this.y = section.getInt("offset.y", 0);
        this.z = section.getInt("offset.z", 0);
    }

    public ParticlesInfo(Particle particle, int count, float x, float y, float z) {
        this.particle = particle;
        this.count = count;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void play(OfflinePlayer player){
        if(player.isOnline()) {
            Player p = player.getPlayer();
            if(p == null) return;
            Vector direction = p.getEyeLocation().getDirection();
            direction = direction.normalize();

            p.spawnParticle(particle, p.getEyeLocation().add(new Vector(direction.getX() * x, direction.getY() * y, direction.getZ() * z)), count);
        }
    }
}
