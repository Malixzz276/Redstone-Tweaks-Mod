package dev.redstonetweaks.gui;

import dev.redstonetweaks.RenderModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class RedstoneTweaksScreen extends Screen {
    private static final int PANEL_W = 300, ROW_H = 32, ROW_GAP = 4, PAD = 12, HEADER_H = 46;
    private static final float OPEN_MS = 300f, CLOSE_MS = 180f, ROW_STAGGER_MS = 45f, ROW_MS = 260f;

    private final Screen parent;
    private final long openNanos = System.nanoTime();
    private long closeNanos;
    private boolean closing;
    private int panelX, panelY, panelH;

    public RedstoneTweaksScreen(Screen parent) {
        super(Component.literal("Redstone Tweaks"));
        this.parent = parent;
    }

    boolean isClosing() { return closing; }

    /** 0..1 overall panel progress (fade + slide), eased in and out. */
    float openProgress() {
        long now = System.nanoTime();
        if (closing) {
            return 1f - Gfx.easeInOut((now - closeNanos) / 1_000_000f / CLOSE_MS);
        }
        return Gfx.easeOutCubic((now - openNanos) / 1_000_000f / OPEN_MS);
    }

    /** Per-row progress; rows pop in one after another when opening. */
    float rowProgress(int index) {
        if (closing) return openProgress();
        float ms = (System.nanoTime() - openNanos) / 1_000_000f - 60f - index * ROW_STAGGER_MS;
        return Gfx.easeOutCubic(ms / ROW_MS);
    }

    int slide() { return (int) ((1f - openProgress()) * 16f); }

    @Override
    protected void init() {
        int rows = RenderModule.values().length;
        panelH = HEADER_H + rows * (ROW_H + ROW_GAP) - ROW_GAP + PAD;
        panelX = (width - PANEL_W) / 2;
        panelY = (height - panelH) / 2;
        int i = 0;
        for (RenderModule m : RenderModule.values()) {
            addRenderableWidget(new ModuleRow(panelX + PAD, panelY + HEADER_H + i * (ROW_H + ROW_GAP),
                    PANEL_W - 2 * PAD, ROW_H, m, i, this));
            i++;
        }
    }

    @Override
    public void onClose() {
        if (!closing) {
            closing = true;
            closeNanos = System.nanoTime();
        }
    }

    @Override
    public boolean isPauseScreen() { return false; }

    // No vanilla blur/background: we draw our own animated dim in render().
    public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float partialTick) { }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        float p = openProgress();
        if (closing && p <= 0f) {
            Minecraft.getInstance().setScreen(parent);
            return;
        }

        g.fill(0, 0, width, height, Gfx.argb((int) (130 * p), 0, 0, 0));

        int y = panelY + slide();
        Gfx.rrect(g, panelX - 1, y - 1, PANEL_W + 2, panelH + 2, 8, Gfx.argb((int) (120 * p), 214, 58, 58));
        Gfx.rrect(g, panelX, y, PANEL_W, panelH, 7, Gfx.argb((int) (240 * p), 22, 22, 27));

        Font font = Minecraft.getInstance().font;
        Gfx.rrect(g, panelX + PAD, y + 14, 8, 8, 4, Gfx.argb((int) (255 * p), 214, 58, 58));
        int title = Gfx.mulAlpha(0xFFFFFFFF, p);
        int sub = Gfx.mulAlpha(0xFF9A9AA6, p);
        if (((title >>> 24) & 255) > 4) g.drawString(font, "Redstone Tweaks", panelX + PAD + 14, y + 13, title);
        if (((sub >>> 24) & 255) > 4) g.drawString(font, "ON = not rendered. Game logic is unchanged.", panelX + PAD, y + 28, sub);

        super.render(g, mouseX, mouseY, partialTick); // draws the rows
    }
}
