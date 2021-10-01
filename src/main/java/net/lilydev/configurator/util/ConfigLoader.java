package net.lilydev.configurator.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import net.fabricmc.loader.api.FabricLoader;
import net.lilydev.configurator.Configurator;
import net.lilydev.configurator.util.SemifinalValue;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;

public class ConfigLoader {
    public static final ArrayList<String> MODULES = new ArrayList<>();

    public static final ArrayList<Block> ENDERMAN_WHITELIST = new ArrayList<>();
    public static final ArrayList<Block> ENDERMAN_BLACKLIST = new ArrayList<>();
    public static final SemifinalValue<String> ENDERMAN_FALLBACK = new SemifinalValue<>("deny");
    public static final SemifinalValue<Boolean> ENDERMAN_ALLOW_UNBREAKABLES = new SemifinalValue<>(false);
    public static final SemifinalValue<Boolean> ENDERMAN_ALLOW_BLOCKENTITIES = new SemifinalValue<>(true);
    public static final SemifinalValue<Boolean> ENDERMAN_ALLOW_PICKUP_UNDERNEATH = new SemifinalValue<>(true);

    public static void load() {}

    static {
        Path configDir = FabricLoader.getInstance().getConfigDir();

        File config = new File(configDir + "/configurator.json");
        JsonParser parser = new JsonParser();

        try {
            JsonElement parsed = parser.parse(new JsonReader(new FileReader(config)));
            if (!parsed.isJsonObject()) {
                throw new JsonSyntaxException("Expected top-level element to be an object!");
            }

            for (Map.Entry<String, JsonElement> entry : parsed.getAsJsonObject().entrySet()) {
                switch (entry.getKey().toLowerCase()) {
                    case "modules" -> {
                        JsonElement moduleConfig = entry.getValue();
                        if (moduleConfig.isJsonObject()) {
                            if (moduleConfig.getAsJsonObject().get("enabled").isJsonArray()) {
                                for (JsonElement module : moduleConfig.getAsJsonObject().get("enabled").getAsJsonArray()) {
                                    if (module.isJsonPrimitive() || !module.getAsJsonPrimitive().isString()) {
                                        MODULES.add(module.getAsString().toLowerCase());
                                    } else {
                                        throw new JsonSyntaxException("Found non-string item in 'modules.enabled' array!");
                                    }
                                }
                            } else {
                                throw new JsonSyntaxException("Expected 'modules.enabled' key to be an array!");
                            }
                        } else {
                            throw new JsonSyntaxException("Expected 'modules' key to be an object!");
                        }

                        for (String moduleName : MODULES) {
                            switch (moduleName.toLowerCase()) {
                                case "enderman" -> {
                                    if (moduleConfig.getAsJsonObject().get("enderman").isJsonObject()) {
                                        JsonObject module = moduleConfig.getAsJsonObject().get("enderman").getAsJsonObject();
                                        for (Map.Entry<String, JsonElement> item : module.entrySet()) {
                                            switch (item.getKey()) {
                                                case "pickup_whitelist" -> {
                                                    JsonElement element = item.getValue();
                                                    if (element instanceof JsonArray array) {
                                                        for (JsonElement identifier : array) {
                                                            if (identifier.isJsonPrimitive() && identifier.getAsJsonPrimitive().isString()) {
                                                                Block block = Registry.BLOCK.get(Identifier.tryParse(identifier.getAsString()));
                                                                ENDERMAN_WHITELIST.add(block);
                                                            }
                                                        }
                                                    } else {
                                                        throw new JsonSyntaxException("Expected 'modules.enderman.pickup_whitelist' key to be an array!");
                                                    }
                                                }

                                                case "pickup_blacklist" -> {
                                                    JsonElement element = item.getValue();
                                                    if (element instanceof JsonArray array) {
                                                        for (JsonElement identifier : array) {
                                                            if (identifier.isJsonPrimitive() && identifier.getAsJsonPrimitive().isString()) {
                                                                Block block = Registry.BLOCK.get(Identifier.tryParse(identifier.getAsString()));
                                                                ENDERMAN_BLACKLIST.add(block);
                                                            }
                                                        }
                                                    } else {
                                                        throw new JsonSyntaxException("Expected 'modules.enderman.pickup_blacklist' key to be an array!");
                                                    }
                                                }

                                                case "pickup_fallback" -> {
                                                    JsonElement element = item.getValue();
                                                    if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                                                        ENDERMAN_FALLBACK.set(element.getAsString());
                                                    }
                                                }

                                                case "allow_block_entities" -> {
                                                    JsonElement element = item.getValue();
                                                    if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean()) {
                                                        ENDERMAN_ALLOW_BLOCKENTITIES.set(element.getAsBoolean());
                                                    }
                                                }

                                                case "allow_unbreakable_blocks" -> {
                                                    JsonElement element = item.getValue();
                                                    if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean()) {
                                                        ENDERMAN_ALLOW_UNBREAKABLES.set(element.getAsBoolean());
                                                    }
                                                }

                                                case "allow_pickup_underneath" -> {
                                                    JsonElement element = item.getValue();
                                                    if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean()) {
                                                        ENDERMAN_ALLOW_PICKUP_UNDERNEATH.set(element.getAsBoolean());
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        throw new JsonSyntaxException("Expected 'modules.enderman' key to be an object!");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (FileNotFoundException|JsonSyntaxException|ClassCastException|IllegalStateException exception) {
            Configurator.LOGGER.fatal("Failed loading config! Reason: " + exception.getMessage());
        }
    }
}
