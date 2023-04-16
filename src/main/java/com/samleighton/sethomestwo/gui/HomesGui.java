package com.samleighton.sethomestwo.gui;

import com.samleighton.sethomestwo.SetHomesTwo;
import com.samleighton.sethomestwo.connections.TeleportationAttemptsConnection;
import com.samleighton.sethomestwo.datatypes.PersistentHome;
import com.samleighton.sethomestwo.models.Home;
import com.samleighton.sethomestwo.models.TeleportAttempt;
import com.samleighton.sethomestwo.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class HomesGui implements Listener {
    private final Inventory inv;

    public HomesGui() {
        inv = Bukkit.createInventory(null, 54, "Your homes");
    }

    /**
     * Draws the blocks for each home in the inventory.
     */
    public void initItems(List<Home> homes) {
        inv.clear();

        if (homes.size() < 1) return;

        for (Home home : homes) {
            String materialName = home.getMaterial();
            Material material = Material.matchMaterial(materialName);
            inv.addItem(createGuiItem(material, home));
        }
    }

    /**
     * Create a new item to be placed in the inventory.
     *
     * @param mat,  The material of the item
     * @param home, The home designated for this item
     * @return ItemStack
     */
    protected ItemStack createGuiItem(final Material mat, Home home) {
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

    /**
     * Open the inventory on the supplied entity
     *
     * @param entity, The entity to open the inventory for.
     */
    public void openInventory(final HumanEntity entity) {
        entity.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
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
        TeleportationAttemptsConnection tac = new TeleportationAttemptsConnection();
        boolean hasAttempt = tac.getLastAttempt(player) != null;

        // Guard to check if player is currently teleporting
        if(hasAttempt) {
            ChatUtils.sendError(player, "You cannot teleport while already teleporting.");
            return;
        }

        // Get the home from the clicked item
        Home home = clickedItemMeta.getPersistentDataContainer().get(homeKey, new PersistentHome());

        // close the inventory
        player.closeInventory();

        // Track player teleport attempt
        tac.createAttempt(new TeleportAttempt(player, player.getLocation()));

        // Send player countdown title.
        Plugin plugin = SetHomesTwo.getPlugin(SetHomesTwo.class);
        int[] seconds = {3};
        // Schedule repeating task for every second
        plugin.getServer().getScheduler().runTaskTimer(plugin, bukkitTask -> {
            // Guard if the player has moved
            TeleportAttempt currAttempt = tac.getLastAttempt(player);
            if(currAttempt != null) {
                if(!currAttempt.canTeleport()){
                    ChatUtils.sendError(player, "Teleport has been cancelled because you have moved.");
                    tac.removeAttempt(player);
                    player.resetTitle();
                    bukkitTask.cancel();
                    return;
                }
            }

            // This logic repeats until the time has expired.
            if(seconds[0] > 0) {
                player.sendTitle(ChatColor.GOLD + "Please stand still", String.format("You will be teleported in %d...", seconds[0]), 0, 999, 0);
                seconds[0]--;
                return;
            }

            // This logic fires after total seconds have elapsed
            assert home != null;
            player.teleport(home.asLocation());
            player.resetTitle();
            tac.removeAttempt(player);
            ChatUtils.sendSuccess(player, String.format("Teleported to %s", home.getName()));
            bukkitTask.cancel();
        }, 0, 20L);
    }

    // Cancel inventory dragging
    @EventHandler
    public void onInventoryDrag(final InventoryDragEvent event) {
        // Cancel all inventory drag events
        if (event.getInventory().equals(inv)) {
            event.setCancelled(true);
        }
    }
}
