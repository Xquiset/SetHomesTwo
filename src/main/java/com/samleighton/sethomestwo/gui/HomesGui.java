package com.samleighton.sethomestwo.gui;

import com.samleighton.sethomestwo.SetHomesTwo;
import com.samleighton.sethomestwo.dao.Dao;
import com.samleighton.sethomestwo.dao.TeleportAttemptsDao;
import com.samleighton.sethomestwo.datatypes.PersistentHome;
import com.samleighton.sethomestwo.enums.UserError;
import com.samleighton.sethomestwo.enums.UserSuccess;
import com.samleighton.sethomestwo.models.Home;
import com.samleighton.sethomestwo.models.TeleportAttempt;
import com.samleighton.sethomestwo.utils.ChatUtils;
import com.samleighton.sethomestwo.utils.ConfigUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class HomesGui implements Listener {
    private final Inventory inv;

    public HomesGui(Player player) {
        String title = ConfigUtil.getConfig().getString("inventoryTitle", "Your homes");
        inv = Bukkit.createInventory(player, 54, title);
    }

    public HomesGui(Player player, String title){
        inv = Bukkit.createInventory(player, 54, title);
    }

    /**
     * Draws the blocks for each home in the inventory.
     */
    public void showHomes(@NotNull List<Home> homes, Player player) {
        inv.clear();

        if (homes.isEmpty()) {
            String noHomesError = ConfigUtil.getConfig().getString("noHomes", UserError.NO_HOMES.getValue());
            ChatUtils.sendError(player, noHomesError);
            return;
        }

        for (Home home : homes) {
            String materialName = home.getMaterial();
            Material material = Material.matchMaterial(materialName);
            inv.addItem(createGuiItem(material, home));
        }

        player.openInventory(inv);
    }

    /**
     * Create a new item to be placed in the inventory.
     *
     * @param mat,  The material of the item
     * @param home, The home designated for this item
     * @return ItemStack
     */
    protected ItemStack createGuiItem(final Material mat, @NotNull Home home) {
        ItemStack item = new ItemStack(mat, 1);
        ItemMeta meta = item.getItemMeta();

        // Setup item lore and display name
        Objects.requireNonNull(meta).setDisplayName(home.getName());
        if(home.getDescription() != null)
            Objects.requireNonNull(meta).setLore(Collections.singletonList(home.getDescription()));

        // Persist the home to the item
        NamespacedKey homeKey = new NamespacedKey(SetHomesTwo.getPlugin(SetHomesTwo.class), "home");
        meta.getPersistentDataContainer().set(homeKey, new PersistentHome(), home);

        // Apply item meta
        item.setItemMeta(meta);

        return item;
    }

    @EventHandler
    public void onInventoryClick(final @NotNull InventoryClickEvent event) {
        // Guard to ensure this is the correct inventory to be using
        if (!event.getInventory().equals(inv)) return;

        // Cancel default click event
        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        NamespacedKey homeKey = new NamespacedKey(SetHomesTwo.getPlugin(SetHomesTwo.class), "home");

        // Guard for user actually clicking item.
        if (clickedItem == null || clickedItem.getType().isAir() || clickedItem.getItemMeta() == null) return;

        // Guard to check if item is actually home destination
        if (!clickedItem.getItemMeta().getPersistentDataContainer().has(homeKey, new PersistentHome())) return;

        ItemMeta clickedItemMeta = clickedItem.getItemMeta();

        // Attempt to get the home from the clicked item
        Home home = clickedItemMeta.getPersistentDataContainer().get(homeKey, new PersistentHome());

        // The home object was not retrievable via item meta
        if(home == null) return;

        // Get the player who clicked the item.
        Player player = (Player) event.getWhoClicked();
        player.closeInventory();

        // Home is blacklisted guard
        if(!home.getCanTeleport()) {
            ChatUtils.sendError(player, ConfigUtil.getConfig().getString("teleportToBlacklistedDimension", UserError.TELEPORT_IS_BLACKLISTED.getValue()));
            return;
        }

        Dao<TeleportAttempt> teleportAttemptsDao = new TeleportAttemptsDao();
        boolean isAlreadyTeleporting = teleportAttemptsDao.get(player) != null;

        // Guard to check if player is currently teleporting
        if (isAlreadyTeleporting) {
            ChatUtils.sendError(player, ConfigUtil.getConfig().getString("teleportedWhileTeleporting", UserError.ALREADY_TELEPORTING.getValue()));
            return;
        }

        // Track player teleport attempt
        teleportAttemptsDao.save(new TeleportAttempt(player, player.getLocation()));

        // Send player countdown title.
        Plugin plugin = SetHomesTwo.getPlugin(SetHomesTwo.class);
        AtomicInteger seconds = new AtomicInteger(ConfigUtil.getConfig().getInt("delay"));

        // Schedule repeating task for every second
        plugin.getServer().getScheduler().runTaskTimer(plugin, bukkitTask -> {
            // Guard to check if task has been cancelled.
            if(bukkitTask.isCancelled()) return;

            // Guard if the player has moved
            TeleportAttempt currAttempt = teleportAttemptsDao.get(player);
            if (currAttempt != null) {
                if (!currAttempt.canTeleport()) {
                    ChatUtils.sendError(player, ConfigUtil.getConfig().getString("movedWhileTeleporting", UserError.MOVED_WHILE_TELEPORTING.getValue()));
                    player.playSound(player, Sound.ENTITY_PLAYER_BIG_FALL, 5f, 5f);
                    teleportAttemptsDao.delete(player.getUniqueId());
                    player.resetTitle();
                    player.removePotionEffect(PotionEffectType.CONFUSION);
                    bukkitTask.cancel();
                    return;
                }
            }

            // This logic repeats until the time has expired.
            if (seconds.get() > 0) {
                String title = ConfigUtil.getConfig().getString("teleportTitle", "Please stand still");
                String subtitle = ConfigUtil.getConfig().getString("teleportSubtitle", "You will be teleported in %d...");
                player.sendTitle(ChatColor.GOLD + title, String.format(subtitle, seconds.get()), 0, 999, 0);
                player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 999, 0 , true));
                player.playNote(player.getLocation(), Instrument.DIDGERIDOO, Note.sharp(2, Note.Tone.F));
                seconds.decrementAndGet();
                return;
            }

            bukkitTask.cancel();
            // This logic fires after total seconds have elapsed
            teleportAttemptsDao.delete(player.getUniqueId());

            player.teleport(home.asLocation());
            player.removePotionEffect(PotionEffectType.CONFUSION);
            player.resetTitle();
            player.playNote(player.getLocation(), Instrument.BELL, Note.sharp(2, Note.Tone.F));
            player.spawnParticle(Particle.PORTAL, player.getLocation(), 100);

            String teleportSuccess = ConfigUtil.getConfig().getString("teleportSuccess", UserSuccess.TELEPORTED.getValue());
            ChatUtils.sendSuccess(player, String.format(teleportSuccess, home.getName()));

        }, 0, 20L);
    }

    // Cancel inventory dragging
    @EventHandler
    public void onInventoryDrag(final @NotNull InventoryDragEvent event) {
        // Cancel all inventory drag events
        if (event.getInventory().equals(inv)) {
            event.setCancelled(true);
        }
    }
}
