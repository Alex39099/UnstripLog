package com.github.alexqp.unstriplog.main;

import org.bukkit.Material;

public class v1_18_R2 extends InternalsProvider {

    protected void setGrassStrippedType() {
        grassStrippedType = Material.DIRT_PATH;
    }

    protected void createGrassOriginBlockTypeSet() {
        grassOriginBlockTypeSet.add(Material.GRASS_BLOCK);
        grassOriginBlockTypeSet.add(Material.DIRT);
        grassOriginBlockTypeSet.add(Material.COARSE_DIRT);
        grassOriginBlockTypeSet.add(Material.MYCELIUM);
        grassOriginBlockTypeSet.add(Material.PODZOL);
        grassOriginBlockTypeSet.add(Material.ROOTED_DIRT);
    }

    protected void createGrassToolTypeSet() {
        grassToolTypeSet.add(Material.DIAMOND_SHOVEL);
        grassToolTypeSet.add(Material.GOLDEN_SHOVEL);
        grassToolTypeSet.add(Material.IRON_SHOVEL);
        grassToolTypeSet.add(Material.STONE_SHOVEL);
        grassToolTypeSet.add(Material.WOODEN_SHOVEL);
        grassToolTypeSet.add(Material.NETHERITE_SHOVEL);
    }

    protected void createLogOriginBlockTypeSet() {
        logOriginBlockTypeSet.add(Material.ACACIA_LOG);
        logOriginBlockTypeSet.add(Material.BIRCH_LOG);
        logOriginBlockTypeSet.add(Material.DARK_OAK_LOG);
        logOriginBlockTypeSet.add(Material.JUNGLE_LOG);
        logOriginBlockTypeSet.add(Material.SPRUCE_LOG);
        logOriginBlockTypeSet.add(Material.OAK_LOG);
        logOriginBlockTypeSet.add(Material.ACACIA_WOOD);
        logOriginBlockTypeSet.add(Material.BIRCH_WOOD);
        logOriginBlockTypeSet.add(Material.DARK_OAK_WOOD);
        logOriginBlockTypeSet.add(Material.JUNGLE_WOOD);
        logOriginBlockTypeSet.add(Material.SPRUCE_WOOD);
        logOriginBlockTypeSet.add(Material.OAK_WOOD);
        logOriginBlockTypeSet.add(Material.WARPED_STEM);
        logOriginBlockTypeSet.add(Material.WARPED_HYPHAE);
        logOriginBlockTypeSet.add(Material.CRIMSON_STEM);
        logOriginBlockTypeSet.add(Material.CRIMSON_HYPHAE);
    }

    protected void createLogToolTypeSet() {
        logToolTypeSet.add(Material.DIAMOND_AXE);
        logToolTypeSet.add(Material.GOLDEN_AXE);
        logToolTypeSet.add(Material.IRON_AXE);
        logToolTypeSet.add(Material.STONE_AXE);
        logToolTypeSet.add(Material.WOODEN_AXE);
        logToolTypeSet.add(Material.NETHERITE_AXE);
    }
}
