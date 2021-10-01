package net.lilydev.configurator;

import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Configurator implements ModInitializer {
    public static final String MOD_ID = "configurator";
    public static final Logger LOGGER = LogManager.getLogger("Configurator");

    @Override
    public void onInitialize() {
        LOGGER.info("Loading Configurator...");
        ConfigLoader.load();

        LOGGER.info("Enabled modules:");
        for (String module : ConfigLoader.MODULES) {
            LOGGER.info("- " + module);
        }
    }
}