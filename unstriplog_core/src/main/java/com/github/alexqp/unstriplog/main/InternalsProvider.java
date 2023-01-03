/*
 * Copyright (C) 2018-2023 Alexander Schmid
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

package com.github.alexqp.unstriplog.main;

import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("WeakerAccess")
public class InternalsProvider {

    protected Material grassStrippedType;

    protected Set<Material> grassOriginBlockTypeSet = new HashSet<>();
    protected Set<Material> grassToolTypeSet = new HashSet<>();

    protected Set<Material> logOriginBlockTypeSet = new HashSet<>();
    protected Set<Material> logStrippedBlockTypeSet = new HashSet<>();
    protected Set<Material> logToolTypeSet = new HashSet<>();

    public InternalsProvider() {
        this.createGrassOriginBlockTypeSet();
        this.setGrassStrippedType();
        this.createGrassToolTypeSet();

        this.createLogOriginBlockTypeSet();
        this.createLogStrippedBlockTypeSet();
        this.createLogToolTypeSet();
    }

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
        logOriginBlockTypeSet.add(Material.MANGROVE_LOG);
        logOriginBlockTypeSet.add(Material.MANGROVE_WOOD);
    }

    protected void createLogToolTypeSet() {
        logToolTypeSet.add(Material.DIAMOND_AXE);
        logToolTypeSet.add(Material.GOLDEN_AXE);
        logToolTypeSet.add(Material.IRON_AXE);
        logToolTypeSet.add(Material.STONE_AXE);
        logToolTypeSet.add(Material.WOODEN_AXE);
        logToolTypeSet.add(Material.NETHERITE_AXE);
    }

    protected void createLogStrippedBlockTypeSet() {
        for (Material mat : this.getLogOriginBlockTypeSet()) {
            String strippedMatName = "STRIPPED_" + mat.name();
            Material strippedMat = Material.matchMaterial(strippedMatName);
            if (strippedMat != null)
                logStrippedBlockTypeSet.add(strippedMat);
        }
    }

    public Set<Material> getGrassOriginBlockTypeSet() {
        return this.grassOriginBlockTypeSet;
    }

    public Material getGrassStrippedType() {
        return this.grassStrippedType;
    }

    public Set<Material> getGrassToolTypeSet() {
        return this.grassToolTypeSet;
    }

    public Set<Material> getLogOriginBlockTypeSet() {
        return this.logOriginBlockTypeSet;
    }

    public Set<Material> getLogStrippedBlockTypeSet() {
        return this.logStrippedBlockTypeSet;
    }

    public Set<Material> getLogToolTypeSet() {
        return this.logToolTypeSet;
    }
}
