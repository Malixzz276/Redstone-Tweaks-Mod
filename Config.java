package dev.redstonetweaks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

public final class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("redstone_tweaks.json");

    private Config() {}

    public static void load() {
        try {
            if (Files.exists(FILE)) {
                JsonObject o = GSON.fromJson(Files.readString(FILE), JsonObject.class);
                if (o != null) {
                    for (RenderModule m : RenderModule.values()) {
                        if (o.has(m.id)) m.enabled = o.get(m.id).getAsBoolean();
                    }
                }
            }
        } catch (Exception e) {
            RedstoneTweaksClient.LOGGER.warn("Could not read config, using defaults", e);
        }
        save();
    }

    public static void save() {
        try {
            JsonObject o = new JsonObject();
            for (RenderModule m : RenderModule.values()) o.addProperty(m.id, m.enabled);
            Files.writeString(FILE, GSON.toJson(o));
        } catch (Exception e) {
            RedstoneTweaksClient.LOGGER.warn("Could not save config", e);
        }
    }
}
