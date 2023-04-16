package com.samleighton.sethomestwo.events;

import com.samleighton.sethomestwo.connections.TeleportationAttemptsConnection;
import com.samleighton.sethomestwo.models.TeleportAttempt;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveWhileTeleporting implements Listener {

    @EventHandler
    public void onPlayerMoveWhileTeleporting(PlayerMoveEvent event) {
        TeleportationAttemptsConnection tac = new TeleportationAttemptsConnection();
        TeleportAttempt ta = tac.getLastAttempt(event.getPlayer());

        // Guard to check if player has attempted a teleport
        if(ta == null) return;
        // Guard to check if player can still teleport
        if(ta.canTeleport()) return;

        Bukkit.getLogger().info(String.format("%s has attempted to move while teleporting.", ta.getPlayer().getDisplayName()));
    }
}
