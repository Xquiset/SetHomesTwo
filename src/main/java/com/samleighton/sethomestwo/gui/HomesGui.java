package com.samleighton.sethomestwo.gui;

import com.samleighton.sethomestwo.SetHomesTwo;
import com.samleighton.sethomestwo.connections.TeleportationAttemptsConnection;
import com.samleighton.sethomestwo.datatypes.PersistentHome;
import com.samleighton.sethomestwo.enums.UserError;
import com.samleighton.sethomestwo.enums.UserSuccess;
import com.samleighton.sethomestwo.models.Home;
import com.samleighton.sethomestwo.models.TeleportAttempt;
import com.samleighton.sethomestwo.utils.ChatUtils;
import com.samleighton.sethomestwo.utils.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class HomesGui implements Listener {
    private final Inventory inv;

    public HomesGui() {
        String title = ConfigUtil.getConfig().getString("inventoryTitle", "Your homes");
        inv = Bukkit.createInventory(null, 54, title);
    }

    /**
     * Draws the blocks for each home in the inventory.
     */
    public void showHomes(@NotNull List<Home> homes, Player player) {
        inv.clear();

        if (homes.size() < 1) {
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
        // Guard for user actually clicking item.
        if (clickedItem == null || clickedItem.getType().isAir() || clickedItem.getItemMeta() == null) return;

        ItemMeta clickedItemMeta = clickedItem.getItemMeta();
        NamespacedKey homeKey = new NamespacedKey(SetHomesTwo.getPlugin(SetHomesTwo.class), "home");

        // Guard to check if item is actually home destination
        if (!clickedItem.getItemMeta().getPersistentDataContainer().has(homeKey, new PersistentHome())) return;

        // Get the player who clicked the item.
        Player player = (Player) event.getWhoClicked();
        player.closeInventory();

        TeleportationAttemptsConnection tac = new TeleportationAttemptsConnection();
        boolean isAlreadyTeleporting = tac.getLastAttempt(player) != null;

        // Guard to check if player is currently teleporting
        if (isAlreadyTeleporting) {
            ChatUtils.sendError(player, ConfigUtil.getConfig().getString("teleportedWhileTeleporting", UserError.ALREADY_TELEPORTING.getValue()));
            return;
        }

        // Get the home from the clicked item
        Home home = clickedItemMeta.getPersistentDataContainer().get(homeKey, new PersistentHome());
        // Track player teleport attempt
        tac.createAttempt(new TeleportAttempt(player, player.getLocation()));

        // Send player countdown title.
        Plugin plugin = SetHomesTwo.getPlugin(SetHomesTwo.class);
        int[] seconds = {ConfigUtil.getConfig().getInt("delay", 3)};
        // Schedule repeating task for every second
        plugin.getServer().getScheduler().runTaskTimer(plugin, bukkitTask -> {
            // Guard if the player has moved
            TeleportAttempt currAttempt = tac.getLastAttempt(player);
            if (currAttempt != null) {
                if (!currAttempt.canTeleport()) {
                    ChatUtils.sendError(player, ConfigUtil.getConfig().getString("movedWhileTeleporting", UserError.MOVED_WHILE_TELEPORTING.getValue()));
                    tac.removeAttempt(player);
                    player.resetTitle();
                    bukkitTask.cancel();
                    return;
                }
            }

            // This logic repeats until the time has expired.
            if (seconds[0] > 0) {
                String title = ConfigUtil.getConfig().getString("teleportTitle", "Please stand still");
                String subtitle = ConfigUtil.getConfig().getString("teleportSubtitle", "You will be teleported in %d...");
                player.sendTitle(ChatColor.GOLD + title, String.format(subtitle, seconds[0]), 0, 999, 0);
                seconds[0]--;
                return;
            }

            // This logic fires after total seconds have elapsed
            assert home != null;
            player.teleport(home.asLocation());
            player.resetTitle();
            tac.removeAttempt(player);
            String teleportSuccess = ConfigUtil.getConfig().getString("teleportSuccess", UserSuccess.TELEPORTED.getValue());
            ChatUtils.sendSuccess(player, String.format(teleportSuccess, home.getName()));
            bukkitTask.cancel();
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
