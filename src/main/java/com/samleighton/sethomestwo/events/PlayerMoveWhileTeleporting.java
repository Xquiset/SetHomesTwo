package com.samleighton.sethomestwo.events;

import com.samleighton.sethomestwo.dao.Dao;
import com.samleighton.sethomestwo.dao.TeleportAttemptsDao;
import com.samleighton.sethomestwo.enums.DebugLevel;
import com.samleighton.sethomestwo.models.TeleportAttempt;
import com.samleighton.sethomestwo.utils.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveWhileTeleporting implements Listener {
    @EventHandler
    public void onPlayerMoveWhileTeleporting(PlayerMoveEvent event) {
        // Skip cancel on move check
        if (!ConfigUtil.getConfig().getBoolean("cancelOnMove", true)) return;

        Dao<TeleportAttempt> teleportAttemptsDaoDao = new TeleportAttemptsDao();
        TeleportAttempt ta = teleportAttemptsDaoDao.get(event.getPlayer());

        // Guard to check if player has attempted a teleport
        if (ta == null) return;
        // Guard to check if player can still teleport
        if (ta.canTeleport()) return;

        if (ConfigUtil.getDebugLevel().equals(DebugLevel.INFO))
            Bukkit.getLogger().info(String.format("%s has attempted to move while teleporting.", ta.getPlayer().getDisplayName()));
    }
}
