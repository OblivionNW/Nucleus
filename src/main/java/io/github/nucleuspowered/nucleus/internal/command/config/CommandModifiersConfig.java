/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.internal.command.config;

import ninja.leaping.configurate.ConfigurationNode;

public class CommandModifiersConfig {

    private static final String WARMUP_NODE = "warmup";
    private static final String COOLDOWN_NODE = "cooldown";
    private static final String COST_NODE = "cost";

    private double cost = -1;
    private int warmup = -1;
    private int cooldown = -1;

    public double getCost() {
        return this.cost;
    }

    public int getWarmup() {
        return this.warmup;
    }

    public int getCooldown() {
        return this.cooldown;
    }

    public void setWarmupEnable(boolean enable) {
        if (enable) {
            this.warmup = Math.max(0, this.warmup);
        } else {
            this.warmup = -1;
        }
    }

    public void setCooldownEnable(boolean enable) {
        if (enable) {
            this.cooldown = Math.max(0, this.cooldown);
        } else {
            this.cooldown = -1;
        }
    }

    public void setCostEnable(boolean enable) {
        if (enable) {
            this.cost = Math.max(0.0, this.cost);
        } else {
            this.cost = -1;
        }
    }

    public void readNode(ConfigurationNode node) {
        ConfigurationNode w = node.getNode(WARMUP_NODE);
        if (w.isVirtual()) {
            this.warmup = -1;
        } else {
            this.warmup = Math.max(0, w.getInt(0));
        }

        w = node.getNode(COOLDOWN_NODE);
        if (w.isVirtual()) {
            this.cooldown = -1;
        } else {
            this.cooldown = Math.max(0, w.getInt(0));
        }

        w = node.getNode(COST_NODE);
        if (w.isVirtual()) {
            this.cost = -1;
        } else {
            this.cost = Math.max(0.0, w.getDouble(0.0));
        }
    }

    public void populateNode(ConfigurationNode node) {
        if (this.warmup < 0) {
            node.removeChild(WARMUP_NODE);
        } else {
            node.getNode(WARMUP_NODE).setValue(this.warmup);
        }

        if (this.cooldown < 0) {
            node.removeChild(COOLDOWN_NODE);
        } else {
            node.getNode(COOLDOWN_NODE).setValue(this.cooldown);
        }

        if (this.cost < 0) {
            node.removeChild(COST_NODE);
        } else {
            node.getNode(COST_NODE).setValue(this.cost);
        }
    }

}
