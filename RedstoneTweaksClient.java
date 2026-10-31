package dev.redstonetweaks;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.brigadier.arguments.BoolArgumentType;
import dev.redstonetweaks.gui.RedstoneTweaksScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedstoneTweaksClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("redstone_tweaks");
    private static boolean openNextTick = false;

    @Override
    public void onInitializeClient() {
        Config.load();

        // Own section in Options > Controls > Key Binds
        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath("redstone_tweaks", "main"));

        KeyMapping openKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.redstone_tweaks.open", InputConstants.Type.KEYSYM,
                InputConstants.KEY_RSHIFT, category));

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (openKey.consumeClick()) {
                if (mc.screen == null) mc.setScreen(new RedstoneTweaksScreen(null));
            }
            if (openNextTick) {
                openNextTick = false;
                mc.setScreen(new RedstoneTweaksScreen(null));
            }
        });

        // /redstonetweaks              -> open the menu
        // /redstonetweaks <module> <true|false> -> toggle without the menu
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            var root = ClientCommandManager.literal("redstonetweaks").executes(ctx -> {
                openNextTick = true; // chat screen would close it if opened immediately
                return 1;
            });
            for (RenderModule m : RenderModule.values()) {
                root.then(ClientCommandManager.literal(m.id)
                        .then(ClientCommandManager.argument("skip", BoolArgumentType.bool()).executes(ctx -> {
                            m.enabled = BoolArgumentType.getBool(ctx, "skip");
                            Config.save();
                            ctx.getSource().sendFeedback(Component.literal(m.displayName + ": " + (m.enabled ? "ON" : "OFF")));
                            return 1;
                        })));
            }
            dispatcher.register(root);
        });
    }
}
