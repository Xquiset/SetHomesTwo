package com.samleighton.sethomestwo.events;

import com.samleighton.sethomestwo.SetHomesTwo;
import com.samleighton.sethomestwo.connections.BlacklistConnection;
import com.samleighton.sethomestwo.connections.HomesConnection;
import com.samleighton.sethomestwo.datatypes.PersistentString;
import com.samleighton.sethomestwo.enums.DebugLevel;
import com.samleighton.sethomestwo.enums.UserError;
import com.samleighton.sethomestwo.gui.HomesGui;
import com.samleighton.sethomestwo.models.Home;
import com.samleighton.sethomestwo.utils.ChatUtils;
import com.samleighton.sethomestwo.utils.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RightClickHomeItem implements Listener {

    private final HomesGui homesGui = new HomesGui();

    @EventHandler
    public void onPlayerRightClickHomeItem(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        Material homeMaterial = Material.matchMaterial(ConfigUtil.getConfig().getString("openHomeItem", Material.COMPASS.name()));

        // Basic item guard
        if ((action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) || event.getItem() == null || event.getItem().getType() != homeMaterial || event.getItem().getItemMeta() == null)
            return;

        // Get item in hand
        ItemStack itemInHand = event.getItem();
        NamespacedKey playerKey = new NamespacedKey(SetHomesTwo.getPlugin(SetHomesTwo.class), "belongs-to");
        NamespacedKey homeKey = new NamespacedKey(SetHomesTwo.getPlugin(SetHomesTwo.class), "list-id");
        PersistentDataContainer itemDataContainer = itemInHand.getItemMeta().getPersistentDataContainer();

        // Guard checking if item is a home item specifically
        if (!(itemDataContainer.has(playerKey, new PersistentString()) && itemDataContainer.has(homeKey, new PersistentString())))
            return;

        // Permission guard
        if(!player.hasPermission("sh2.teleport")){
            ChatUtils.invalidPermissions(player);
            return;
        }

        String playerUUID = itemDataContainer.get(playerKey, new PersistentString());
        String homeItemUUID = itemDataContainer.get(homeKey, new PersistentString());

        // Guard to make sure only the player who owns this item can open it.
        if (!Objects.equals(playerUUID, player.getUniqueId().toString())) {
            String falseHomeItemError = ConfigUtil.getConfig().getString("falseHomeItem", UserError.INVALID_HOME_ITEM.getValue());
            player.sendMessage(falseHomeItemError);
            return;
        }

        BlacklistConnection blacklistConnection = new BlacklistConnection();
        HomesConnection homesConnection = new HomesConnection();
        List<Home> playersHomes = homesConnection.getPlayersHomes(player.getUniqueId().toString());
        for (Home playersHome : playersHomes) {
            String dimension = playersHome.getDimension();
            List<String> blacklistedDimensions = blacklistConnection.getBlacklistedDimensions();
            Map<String, String> blacklistedMap = blacklistConnection.getDimensionsMap();

            if (blacklistedDimensions.contains(blacklistedMap.get(dimension))) {
                playersHome.setDescription("Cannot teleport here: dimension blacklisted");
                playersHome.setCanTeleport(false);
            }
        }
        this.homesGui.showHomes(playersHomes, player);

        if (ConfigUtil.getDebugLevel().equals(DebugLevel.INFO))
            Bukkit.getLogger().info(String.format("%s has clicked with home item id %s", player.getDisplayName(), homeItemUUID));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        this.homesGui.onInventoryClick(event);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        this.homesGui.onInventoryDrag(event);
    }
}
