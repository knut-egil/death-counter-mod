package dev.knutegil.death_counter.api;

import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;

@FunctionalInterface
public interface DeathMessagePacketCallback {
    void onDeathMessage(DeathMessageS2CPacket packet);
}