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

package com.github.alexqp.unstriplog.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;
import com.github.alexqp.unstriplog.main.InternalsProvider;
import org.jetbrains.annotations.Nullable;

public class GrassStripListener extends BlockStripListener {

    private final InternalsProvider internals;

    public GrassStripListener(JavaPlugin plugin, InternalsProvider internals, int delay, boolean mustSneakUnstrip, boolean mustSneakStrip) {
        super(plugin, delay, mustSneakUnstrip, mustSneakStrip, "unstriplog.path");
        this.internals = internals;
    }

    @Override
    void performUnstripping(Block block, @Nullable Material newBlockType) {
        block.setType(newBlockType != null ? newBlockType : Material.DIRT);
    }

    @Override
    boolean isTooled(Material mat) {
        return internals.getGrassToolTypeSet().contains(mat);
    }

    @Override
    boolean isOriginBlockType(Material mat) {
        return internals.getGrassOriginBlockTypeSet().contains(mat);
    }

    @Override
    boolean isStrippedBlockType(Material mat) {
        return internals.getGrassStrippedType().equals(mat);
    }
}
