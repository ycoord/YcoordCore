package ru.ycoord.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import ru.ycoord.placeholder.IPlaceholderAPI;

public class PlaceholderEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private IPlaceholderAPI p = null;

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public void setPlaceholder(IPlaceholderAPI p){
        this.p = p;
    }

    public IPlaceholderAPI getPlaceholder(){
        return p;
    }
}
