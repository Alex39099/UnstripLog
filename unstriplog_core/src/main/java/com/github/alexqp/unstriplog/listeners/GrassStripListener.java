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
