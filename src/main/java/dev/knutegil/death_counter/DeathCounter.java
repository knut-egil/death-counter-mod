package dev.knutegil.death_counter;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.registry.Registries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class DeathCounter implements ModInitializer {
	public static final String MOD_ID = "death-counter";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Optional<ModContainer> MOD = FabricLoader.getInstance().getModContainer(MOD_ID);

	@Override
	public void onInitialize() {
        // Guard against empty mod container
        if (MOD.isEmpty()) return;

        // Mod initialized
		LOGGER.info("{} initialized", MOD.get().getMetadata().getName());
	}
}