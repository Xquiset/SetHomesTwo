package com.samleighton.sethomestwo.gui;

import com.samleighton.sethomestwo.models.Home;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
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

        if(homes.size() < 1) return;

        for(Home home : homes){
            String materialName = home.getMaterial();
            Material material = Material.matchMaterial(materialName);
            inv.addItem(createGuiItem(material, home.getName(), home.getDescription()));
        }
    }

    /**
     * Create a new item to be placed in the inventory.
     *
     * @param mat,  The material of the item
     * @param name, The name for the item
     * @param lore, The lore for the item
     * @return ItemStack
     */
    protected ItemStack createGuiItem(final Material mat, final String name, final String... lore) {
        ItemStack item = new ItemStack(mat, 1);
        ItemMeta meta = item.getItemMeta();

        // Setup item lore and display name
        Objects.requireNonNull(meta).setDisplayName(name);
        Objects.requireNonNull(meta).setLore(Arrays.asList(lore));

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
        if (clickedItem == null || clickedItem.getType().isAir()) return;

        // Get the player who clicked the item.
        Player p = (Player) event.getWhoClicked();
        p.sendMessage("You clicked slot " + event.getRawSlot());
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
