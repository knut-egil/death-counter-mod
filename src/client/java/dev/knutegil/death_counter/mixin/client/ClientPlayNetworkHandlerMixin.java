package dev.knutegil.death_counter.mixin.client;

import dev.knutegil.death_counter.api.DeathCounterEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.StatisticsS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject (at = @At("TAIL"), method = "onStatistics")
    private void onStatistics(StatisticsS2CPacket packet, CallbackInfo ci) {
        DeathCounterEvents.STATISTICS_RECEIVED.invoker().onStatistics(packet);
    }

    @Inject (at = @At("TAIL"), method = "onDeathMessage")
    private void onDeathMessage(DeathMessageS2CPacket packet, CallbackInfo ci) {
        DeathCounterEvents.DEATH_MESSAGE_RECEIVED.invoker().onDeathMessage(packet);
    }
}