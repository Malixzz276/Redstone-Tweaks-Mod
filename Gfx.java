package dev.redstonetweaks.gui;

import net.minecraft.client.gui.GuiGraphics;

/** Tiny drawing/easing helpers. Only uses GuiGraphics.fill so it stays version-proof. */
final class Gfx {
    private Gfx() {}

    static float clamp01(float v) { return v < 0f ? 0f : Math.min(v, 1f); }
    static float easeOutCubic(float t) { t = clamp01(t); float u = 1f - t; return 1f - u * u * u; }
    static float easeInOut(float t) { t = clamp01(t); return t * t * (3f - 2f * t); }

    static int argb(int a, int r, int g, int b) {
        return (Math.max(0, Math.min(255, a)) << 24) | (r << 16) | (g << 8) | b;
    }

    static int mulAlpha(int color, float a) {
        int alpha = (int) ((color >>> 24) * clamp01(a));
        return (alpha << 24) | (color & 0xFFFFFF);
    }

    static int lerp(int c1, int c2, float t) {
        t = clamp01(t);
        int a = (int) (((c1 >>> 24) & 255) + ((((c2 >>> 24) & 255) - ((c1 >>> 24) & 255)) * t));
        int r = (int) (((c1 >> 16) & 255) + ((((c2 >> 16) & 255) - ((c1 >> 16) & 255)) * t));
        int g = (int) (((c1 >> 8) & 255) + ((((c2 >> 8) & 255) - ((c1 >> 8) & 255)) * t));
        int b = (int) ((c1 & 255) + (((c2 & 255) - (c1 & 255)) * t));
        return argb(a, r, g, b);
    }

    /** Filled rounded rectangle built from scanlines. */
    static void rrect(GuiGraphics g, int x, int y, int w, int h, int r, int color) {
        if (((color >>> 24) & 255) == 0) return;
        r = Math.min(r, Math.min(w, h) / 2);
        if (r <= 0) { g.fill(x, y, x + w, y + h, color); return; }
        g.fill(x, y + r, x + w, y + h - r, color);
        for (int i = 0; i < r; i++) {
            double dy = r - i - 0.5;
            int inset = (int) Math.round(r - Math.sqrt(r * r - dy * dy));
            g.fill(x + inset, y + i, x + w - inset, y + i + 1, color);
            g.fill(x + inset, y + h - i - 1, x + w - inset, y + h - i, color);
        }
    }
}
