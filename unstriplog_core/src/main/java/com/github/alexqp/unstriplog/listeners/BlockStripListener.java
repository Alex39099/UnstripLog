/*
 * Copyright (C) 2018-2024 Alexander Schmid
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.github.alexqp.unstriplog.listeners;

import com.github.alexqp.commons.messages.ConsoleMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public abstract class BlockStripListener implements Listener {

    private static final String blockMetaDataKey = "unstriplog_old_blockType";

    private final JavaPlugin plugin;
    private boolean infinite = false;
    private final int delay;
    private final boolean mustSneakStrip;
    private final boolean mustSneakUnstrip;

    private final String unstripPermission;

    private final Map<Location, BukkitRunnable> blockLocs = new HashMap<>();

    BlockStripListener(JavaPlugin plugin, int delay, boolean mustSneakUnstrip, boolean mustSneakStrip, String unstripPermission) {
        this.plugin = plugin;
        this.delay = delay;
        this.mustSneakUnstrip = mustSneakUnstrip;
        this.mustSneakStrip = mustSneakStrip;
        this.unstripPermission = unstripPermission;
        if (this.delay == -1)
            this.infinite = true;
    }

    private void cancelBlockLocationRemoval(Block block) {
        Location bLoc = block.getLocation();
        BukkitRunnable runnable = blockLocs.get(bLoc);
        if (runnable != null) {
            runnable.cancel();
            ConsoleMessage.debug(this.getClass(), plugin, "cancelled old task to remove block location for type " + block.getType());
            blockLocs.remove(bLoc);
        }
    }

    private void setOldBlockType(Block block) {
        block.setMetadata(blockMetaDataKey, new FixedMetadataValue(plugin, block.getType().name()));
    }

    @Nullable
    private Material getAndDeleteOldBlockType(Block block) {
        if (block.hasMetadata(blockMetaDataKey)) {
            Material mat = Material.matchMaterial(block.getMetadata(blockMetaDataKey).get(0).asString());
            block.removeMetadata(blockMetaDataKey, plugin);
            return this.isOriginBlockType(mat) ? mat : null; // different listeners may interfere with one another! (infinite on, strip wood, replace it with dirt, unstrip log)
        }
        return null;
    }

    private void addBlockLocation(Block block) {
        if (this.infinite || delay == 0)
            return;

        BlockStripListener listener = this;
        Location bLoc = block.getLocation();

        this.cancelBlockLocationRemoval(block);

        ConsoleMessage.debug(listener.getClass(), plugin, "added block location for type " + block.getType());
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                blockLocs.remove(bLoc);
                ConsoleMessage.debug(listener.getClass(), plugin, "removed block location for type " + block.getType());
            }
        };
        blockLocs.put(bLoc, runnable);
        runnable.runTaskLater(plugin, delay);
    }

    abstract void performUnstripping(Block block, @Nullable Material newBlockType);
    abstract boolean isTooled(Material mat);
    abstract boolean isOriginBlockType(Material mat);
    abstract boolean isStrippedBlockType(Material mat);

    private void performDelayedUnstripping(Block block, @Nullable Material newBlockType) {
        ConsoleMessage.debug(this.getClass(), plugin, "delayed unstripping...");
        new BukkitRunnable() {
            @Override
            public void run() {
                performUnstripping(block, newBlockType);
            }
        }.runTask(plugin);
    }


    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || !e.hasBlock() || !e.hasItem()) {
            ConsoleMessage.debug(this.getClass(), plugin, "Event cancelled because of no right-click/block/item " + e.getHand() + ", block == " + e.hasBlock() + ", item == " + e.hasItem());
            return;
        }

        assert e.getHand() != null; // is only null for Action.PHYSICAL
        if (e.getHand().equals(EquipmentSlot.OFF_HAND) && this.isTooled(e.getPlayer().getInventory().getItemInMainHand().getType())) {
            ConsoleMessage.debug(this.getClass(), plugin, "Event cancelled over main-hand-priority " + e.getHand());
            return;
        }

        assert e.getItem() != null; // checked in first if: !e.hasItem
        if (this.isTooled(e.getItem().getType())) {
            Block block = e.getClickedBlock();
            assert block != null; // checked in first if: !e.hasBlock
            if (this.isOriginBlockType(block.getType())) {
                if (this.mustSneakStrip && !e.getPlayer().isSneaking()) {
                    ConsoleMessage.debug(this.getClass(), plugin, "Event cancelled over not sneaking (stripping) " + e.getHand());
                    e.setCancelled(true);
                    return;
                }
                this.setOldBlockType(block);
                this.addBlockLocation(block);
                return;
            } else if (this.isStrippedBlockType(block.getType())) {
                if (this.infinite || blockLocs.containsKey(block.getLocation())) {
                    if (this.mustSneakUnstrip && !e.getPlayer().isSneaking()) {
                        ConsoleMessage.debug(this.getClass(), plugin, "Event cancelled over not sneaking (unstripping) " + e.getHand());
                        return;
                    }

                    if (!e.getPlayer().hasPermission(unstripPermission)) {
                        ConsoleMessage.debug(this.getClass(), plugin, "Event cancelled over no permission " + e.getHand());
                        return;
                    }
                    this.cancelBlockLocationRemoval(block);
                    this.performDelayedUnstripping(block, this.getAndDeleteOldBlockType(block));
                    return;
                }
                ConsoleMessage.debug(this.getClass(), plugin, "Event cancelled over blockLoc is not available / no infinite mode " + e.getHand());
                return;
            }
            ConsoleMessage.debug(this.getClass(), plugin, "Event cancelled over isStrippedBlockType == false (" + block.getType() + ") " + e.getHand());
            return;
        }
        ConsoleMessage.debug(this.getClass(), plugin, "Event cancelled over not tooled " + e.getHand());
    }

    private void onBlockEvent(Block block) {
        this.cancelBlockLocationRemoval(block);
        this.getAndDeleteOldBlockType(block);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent e) {
        this.onBlockEvent(e.getBlock());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBurn(BlockBurnEvent e) {
        this.onBlockEvent(e.getBlock());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockExplode(BlockExplodeEvent e) {
        for (Block block : e.blockList()) {
            this.onBlockEvent(block);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockMove(BlockPistonExtendEvent e) {
        for (Block block : e.getBlocks()) {
            this.onBlockEvent(block);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockMove(BlockPistonRetractEvent e) {
        for (Block block : e.getBlocks()) {
            this.onBlockEvent(block);
        }
    }
}
