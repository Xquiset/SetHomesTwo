package com.samleighton.sethomestwo.models;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TeleportAttempt {
    private final Location location;
    private Player player;
    private boolean canTeleport = true;

    public TeleportAttempt(Player player, Location location) {
        this.location = location;
        this.player = player;
    }


    public Location getLocation() {
        return this.location;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setCanTeleport(boolean canTeleport) {
        this.canTeleport = canTeleport;
    }

    public boolean canTeleport() {
        return this.canTeleport;
    }
}
