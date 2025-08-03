package ru.ycoord.core.gui.animation;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.ycoord.core.gui.GuiBase;
import ru.ycoord.core.messages.MessageBase;
import ru.ycoord.core.messages.MessagePlaceholders;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class Animation {
    protected final ConfigurationSection section;
    protected final int duration;
    private boolean background = false;
    private Material backgroundMaterial = Material.AIR;
    private String backgroundName = "";

    public Animation(ConfigurationSection section) {
        this.section = section;
        this.duration = section.getInt("duration", 10);
        ConfigurationSection backgroundSection = section.getConfigurationSection("background");
        if (backgroundSection != null) {
            this.background = backgroundSection.getBoolean("enabled", false);
            this.backgroundName = backgroundSection.getString("name", "");
            try {
                String material = backgroundSection.getString("material", "BLACK_STAINED_GLASS_PANE");
                this.backgroundMaterial = Material.valueOf(material);
            }catch (Exception ignored){

            }
        }
    }

    void sleep() {
        try {
            Thread.sleep(duration);
        } catch (Exception ignored) {

        }
    }

    public void animate(GuiBase base, Inventory inventory, Player player, HashMap<Integer, GuiBase.GuiItemCharacter> guiElements, MessagePlaceholders messagePlaceholders) {
        if (background) {
            for (int i = 0; i < inventory.getSize(); i++) {
                ItemStack item = new ItemStack(backgroundMaterial);
                ItemMeta meta = item.getItemMeta();
                meta.displayName(Component.text(MessageBase.translateColor(backgroundName, new MessagePlaceholders(player))));
                item.setItemMeta(meta);
                inventory.setItem(i, item);
            }
        }
        List<List<Integer>> data = makeFrames(9, inventory.getSize() / 9);

        CompletableFuture.runAsync(() -> {
            for (List<Integer> slots : data) {
                for (Integer slot : slots) {
                    if (!guiElements.containsKey(slot))
                        continue;
                    if (guiElements.get(slot).item == null)
                        return;
                    ItemStack i = guiElements.get(slot).item.buildItem(player, base, slot, messagePlaceholders);
                    if (i == null)
                        return;
                    inventory.setItem(slot, i);
                }
                sleep();
            }
        });
    }

    protected List<List<Integer>> makeFrames(int w, int h) {
        return new LinkedList<>();
    }

    protected int makeIndex(int x, int y, int w) {
        return y * w + x;
    }
}
