package dev.knutegil.death_counter;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.knutegil.death_counter.api.DeathCounterEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.StatisticsS2CPacket;
import net.minecraft.stat.Stats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.nio.charset.StandardCharsets;

public class DeathCounterClient implements ClientModInitializer {
    public static final String MOD_ID = "death-counter";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static MinecraftClient CLIENT;

	@Override
	public void onInitializeClient() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            CLIENT = client;
            sendStatisticsRequest(client);
        });
        DeathCounterEvents.STATISTICS_RECEIVED.register(this::onStatisticsReceived);
        DeathCounterEvents.DEATH_MESSAGE_RECEIVED.register(this::onDeathMessageReceived);
    }

    private int DEATH_COUNT = 0;

    private void onStatisticsReceived(StatisticsS2CPacket packet) {
        var client = CLIENT;
        if (client.player == null) return;

        // Get stat handler
        var statHandler = client.player.getStatHandler();

        // Get "deaths" stat, update global death count variable
        DEATH_COUNT = statHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.DEATHS));

        // Log death count
        LOGGER.info("(onStatisticsReceived) Total player deaths: {}", DEATH_COUNT);

        // Broadcast death count data
        try {
            var playerUUID = client.player.getUuid().toString();
            broadcastDeathCount(playerUUID, DEATH_COUNT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void onDeathMessageReceived(DeathMessageS2CPacket packet) {
        var client = CLIENT;
        if (client.player == null) return;

        // Increment DEATH_COUNT
        DEATH_COUNT++;

        // Log death count
        LOGGER.info("(OnDeathMessageReceived) Total player deaths: {}", DEATH_COUNT);

        // Broadcast death count data
        try {
            var playerUUID = client.player.getUuid().toString();
            broadcastDeathCount(playerUUID, DEATH_COUNT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void sendStatisticsRequest(MinecraftClient client) {
        // Get client  player
        var player = client.player;
        if (player == null) return;

        // Get network handler
        var networkHandler = client.getNetworkHandler();
        if (networkHandler == null) return;

        // Send statistics request packet  to server
        ClientStatusC2SPacket requestStatsPacket = new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.REQUEST_STATS);
        networkHandler.sendPacket(requestStatsPacket);
    }

    private static final String BROADCAST_HOST = "255.255.255.255";
    private void broadcastDeathCount(String playerUUID, int deathCount) throws Exception {
        // TODO: Add some repeated broadcasts for deliverability reasons

        // TODO: Add proper fabric settings for modifying broadcast settings

        var payload = new DeathCounterBroadcastPayload(playerUUID, deathCount, System.currentTimeMillis());
        var serialized = DeathCounterBroadcastPayload.CODEC.encodeStart(JsonOps.INSTANCE, payload).result();
        if (serialized.isEmpty()) throw new Exception("The encoded death counter broadcast payload was empty");

        var json = serialized.get();
        var buffer = json.toString().getBytes(StandardCharsets.UTF_8);

        var target = new InetSocketAddress(BROADCAST_HOST, 33396);
        var packet = new DatagramPacket(buffer, buffer.length, target);

        // Create UDP socket
        var socket = new DatagramSocket();
        socket.send(packet);

        // Log
        LOGGER.info("Death counter data broadcast sent");
    }

    private record DeathCounterBroadcastPayload(String playerUUID, int deathCount, long timestamp) {
        public static final Codec<DeathCounterBroadcastPayload> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("player_uuid").forGetter(DeathCounterBroadcastPayload::playerUUID),
                    Codec.INT.fieldOf("death_count").forGetter(DeathCounterBroadcastPayload::deathCount),
                    Codec.LONG.fieldOf("timestamp").forGetter(DeathCounterBroadcastPayload::timestamp)
            ).apply(instance, DeathCounterBroadcastPayload::new));
        }
}