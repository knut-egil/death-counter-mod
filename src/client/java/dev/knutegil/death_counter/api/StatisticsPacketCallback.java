package dev.knutegil.death_counter.api;

import net.minecraft.network.packet.s2c.play.StatisticsS2CPacket;

@FunctionalInterface
public interface StatisticsPacketCallback {
    void onStatistics(StatisticsS2CPacket packet);
}