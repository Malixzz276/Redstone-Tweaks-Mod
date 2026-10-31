package dev.redstonetweaks.mixin;

import dev.redstonetweaks.RenderModule;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.PrimedTnt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Primed TNT entities still exist and tick/explode, they just aren't drawn. */
@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void lr$skipTnt(Entity entity, Frustum frustum, double camX, double camY, double camZ,
                            CallbackInfoReturnable<Boolean> cir) {
        if (RenderModule.TNT.enabled && entity instanceof PrimedTnt) {
            cir.setReturnValue(false);
        }
    }
}
