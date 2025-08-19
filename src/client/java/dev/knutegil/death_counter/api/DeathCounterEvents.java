package dev.knutegil.death_counter.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.packet.s2c.play.StatisticsS2CPacket;

public class DeathCounterEvents {
    public static final Event<StatisticsPacketCallback> STATISTICS_RECEIVED =
            EventFactory.createArrayBacked(StatisticsPacketCallback.class,
                    (listeners) -> (packet) -> {
                        for (StatisticsPacketCallback listener : listeners) {
                            listener.onStatistics(packet);
                        }
                    });
    public static final Event<DeathMessagePacketCallback> DEATH_MESSAGE_RECEIVED =
            EventFactory.createArrayBacked(DeathMessagePacketCallback.class,
                    (listeners) -> (packet) -> {
                        for (DeathMessagePacketCallback listener : listeners) {
                            listener.onDeathMessage(packet);
                        }
                    });
}
