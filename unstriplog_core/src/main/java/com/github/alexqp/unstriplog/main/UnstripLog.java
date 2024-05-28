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

package com.github.alexqp.unstriplog.main;

import com.github.alexqp.commons.bstats.bukkit.Metrics;
import com.github.alexqp.commons.config.ConfigChecker;
import com.github.alexqp.commons.config.ConsoleErrorType;
import com.github.alexqp.commons.messages.ConsoleMessage;
import com.github.alexqp.commons.messages.Debugable;
import com.google.common.collect.Range;

import com.jeff_media.updatechecker.UpdateCheckSource;
import com.jeff_media.updatechecker.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import com.github.alexqp.unstriplog.listeners.GrassStripListener;
import com.github.alexqp.unstriplog.listeners.LogStripListener;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class UnstripLog extends JavaPlugin implements Debugable {

    @Override
    public boolean getDebug() {
        return false;
    }

    private static final String defaultInternalsVersion = "v1_20_R3";
    private static InternalsProvider internals;
    static {
        try {
            String packageName = UnstripLog.class.getPackage().getName();
            String internalsName = getInternalsName(Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3]);
            if (internalsName.equals(defaultInternalsVersion)) {
                Bukkit.getLogger().log(Level.INFO, "UnstripLog is using the latest implementation (last tested for " + defaultInternalsVersion + ").");
                internals = new InternalsProvider();
            } else {
                Bukkit.getLogger().log(Level.INFO, UnstripLog.class.getSimpleName() + " is using the implementation for version " + internalsName + ".");
                internals = (InternalsProvider) Class.forName(packageName + "." + internalsName).getDeclaredConstructor().newInstance();
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | ClassCastException | NoSuchMethodException | InvocationTargetException exception) {
            Bukkit.getLogger().log(Level.WARNING, UnstripLog.class.getSimpleName() + " could not find an updated implementation for this server version. " +
                    "However the plugin is trying to use the latest implementation which should work if Minecraft did not change drastically (last tested version: " + defaultInternalsVersion + ").");
            internals = new InternalsProvider();
        }
    }

    private static String getInternalsName(String internalsName) {
        Map<String, String> internalsVersions = new HashMap<>();
        internalsVersions.put("v1_13_R1", "v1_15_R1");
        internalsVersions.put("v1_13_R2", "v1_15_R1");
        internalsVersions.put("v1_14_R1", "v1_15_R1");
        internalsVersions.put("v1_15_R1", "v1_15_R1");
        // added netherite tools, nether wood
        internalsVersions.put("v1_16_R1", "v1_16_R3");
        internalsVersions.put("v1_16_R2", "v1_16_R3");
        internalsVersions.put("v1_16_R3", "v1_16_R3");
        // renamed GRASS_PATH to DIRT_PATH, added ROOTED_DIRT
        internalsVersions.put("v1_17_R1", "v1_18_R2");
        internalsVersions.put("v1_18_R1", "v1_18_R2");
        internalsVersions.put("v1_18_R2", "v1_18_R2");
        // added mangrove wood
        internalsVersions.put("v1_19_R1", "v1_19_R3");
        internalsVersions.put("v1_19_R2", "v1_19_R3");
        internalsVersions.put("v1_19_R3", "v1_19_R3");
        // added bamboo, cherry wood
        return internalsVersions.getOrDefault(internalsName, defaultInternalsVersion);
    }

    @Override
    public void onEnable() {
        new Metrics(this, 3642);
        this.saveDefaultConfig();
        this.getLogger().info("This plugin was made by alex_qp.");
        this.updateChecker();

        ConfigChecker configChecker = new ConfigChecker(this);
        // be aware that values in (-1, 0) are not possible because value is integer!
        int grassDelay = configChecker.checkInt(this.getConfig(), "timeframe_path", ConsoleErrorType.WARN, 100, Range.atLeast(-1));
        int logDelay = configChecker.checkInt(this.getConfig(), "timeframe_wood", ConsoleErrorType.WARN, 100, Range.atLeast(-1));
        boolean mustSneakUnstrip = configChecker.checkBoolean(this.getConfig(), "must_sneak_unstrip", ConsoleErrorType.WARN, true);
        boolean mustSneakStrip = configChecker.checkBoolean(this.getConfig(), "must_sneak_strip", ConsoleErrorType.WARN, false);

        boolean keepEnabled = false;


        if (grassDelay != 0 || mustSneakStrip) {
            Bukkit.getPluginManager().registerEvents(new GrassStripListener(this, internals, grassDelay, mustSneakUnstrip, mustSneakStrip), this);
            if (grassDelay != 0)
                this.getLogger().info("Unstripping of " + internals.getGrassStrippedType().name().toLowerCase() + " enabled with delay " + grassDelay);
            keepEnabled = true;
        }

        if (logDelay != 0 || mustSneakStrip) {
            Bukkit.getPluginManager().registerEvents(new LogStripListener(this, internals, logDelay, mustSneakUnstrip, mustSneakStrip), this);
            if (logDelay != 0)
                this.getLogger().info("Unstripping of stripped log/wood enabled with delay " + logDelay);
            keepEnabled = true;
        }

        if (!keepEnabled)
            this.onDisable();
    }

    private void updateChecker() {
        int spigotResourceID = 62738;
        ConfigChecker configChecker = new ConfigChecker(this);
        ConfigurationSection updateCheckerSection = configChecker.checkConfigSection(this.getConfig(), "updatechecker", ConsoleErrorType.ERROR);
        if (updateCheckerSection != null && configChecker.checkBoolean(updateCheckerSection, "enable", ConsoleErrorType.WARN, true)) {
            ConsoleMessage.debug((Debugable) this, "enabled UpdateChecker");

            new UpdateChecker(this, UpdateCheckSource.SPIGOT, String.valueOf(spigotResourceID))
                    .setDownloadLink(spigotResourceID)
                    .setChangelogLink("https://www.spigotmc.org/resources/" + spigotResourceID + "/updates")
                    .setDonationLink("https://paypal.me/alexqpplugins")
                    .setNotifyOpsOnJoin(configChecker.checkBoolean(updateCheckerSection, "notify_op_on_login", ConsoleErrorType.WARN, true))
                    .setNotifyByPermissionOnJoin("betterconcrete.updatechecker")
                    .checkEveryXHours(24).checkNow();
        }
    }
}
