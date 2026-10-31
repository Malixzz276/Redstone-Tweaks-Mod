package dev.redstonetweaks.gui;

import dev.redstonetweaks.Config;
import dev.redstonetweaks.RenderModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

/** One menu row: label, description and an animated on/off switch. Click = toggle. */
class ModuleRow extends Button {
    private static final int TRACK_OFF = 0xFF4A4A54;
    private static final int TRACK_ON = 0xFFD63A3A; // redstone red
    private static final int ROW_BG = 0xFF2A2A31;
    private static final int ROW_BG_HOVER = 0xFF383842;

    private final RenderModule module;
    private final int index;
    private final RedstoneTweaksScreen screen;
    private float switchAnim;
    private float hoverAnim;
    private long lastNanos = 0;

    ModuleRow(int x, int y, int w, int h, RenderModule module, int index, RedstoneTweaksScreen screen) {
        super(x, y, w, h, Component.literal(module.displayName), b -> {
            module.enabled = !module.enabled;
            Config.save();
        }, DEFAULT_NARRATION);
        this.module = module;
        this.index = index;
        this.screen = screen;
        this.switchAnim = module.enabled ? 1f : 0f;
    }

    @Override
    public void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        long now = System.nanoTime();
        float dt = lastNanos == 0 ? 0f : Math.min((now - lastNanos) / 1_000_000_000f, 0.1f);
        lastNanos = now;

        // frame-rate independent easing toward target
        float target = module.enabled ? 1f : 0f;
        switchAnim += (target - switchAnim) * (1f - (float) Math.exp(-dt * 13f));
        if (Math.abs(target - switchAnim) < 0.002f) switchAnim = target;

        boolean hover = !screen.isClosing()
                && mouseX >= getX() && mouseX < getX() + getWidth()
                && mouseY >= getY() && mouseY < getY() + getHeight();
        hoverAnim += ((hover ? 1f : 0f) - hoverAnim) * (1f - (float) Math.exp(-dt * 18f));

        float rp = screen.rowProgress(index);
        if (rp <= 0.01f) return;

        int x = getX();
        int y = getY() + (int) ((1f - rp) * 10f) + screen.slide();
        int w = getWidth(), h = getHeight();
        Font font = Minecraft.getInstance().font;

        Gfx.rrect(g, x, y, w, h, 5, Gfx.mulAlpha(Gfx.lerp(ROW_BG, ROW_BG_HOVER, hoverAnim), rp));

        int titleColor = Gfx.mulAlpha(0xFFFFFFFF, rp);
        int descColor = Gfx.mulAlpha(0xFF9A9AA6, rp);
        if (((titleColor >>> 24) & 255) > 4) g.drawString(font, module.displayName, x + 10, y + 6, titleColor);
        if (((descColor >>> 24) & 255) > 4) g.drawString(font, module.description, x + 10, y + 18, descColor);

        // switch
        float e = Gfx.easeInOut(switchAnim);
        int sw = 32, sh = 16;
        int sx = x + w - sw - 10;
        int sy = y + (h - sh) / 2;
        Gfx.rrect(g, sx, sy, sw, sh, 8, Gfx.mulAlpha(Gfx.lerp(TRACK_OFF, TRACK_ON, e), rp));
        int knob = 12;
        int kx = sx + 2 + Math.round((sw - 4 - knob) * e);
        Gfx.rrect(g, kx, sy + 2, knob, knob, 6, Gfx.mulAlpha(Gfx.lerp(0xFFD0D0D6, 0xFFFFFFFF, e), rp));
    }
}
