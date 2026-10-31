package dev.redstonetweaks.mixin;

import dev.redstonetweaks.RenderModule;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Note block notes + TNT explosion particles are never created client-side. */
@Mixin(ParticleEngine.class)
public class ParticleEngineMixin {
    @Inject(method = "createParticle", at = @At("HEAD"), cancellable = true)
    private void lr$filter(ParticleOptions options, double x, double y, double z,
                           double xa, double ya, double za, CallbackInfoReturnable<Particle> cir) {
        ParticleType<?> t = options.getType();
        if ((RenderModule.NOTE_BLOCK.enabled && t == ParticleTypes.NOTE)
                || (RenderModule.TNT.enabled && (t == ParticleTypes.EXPLOSION || t == ParticleTypes.EXPLOSION_EMITTER))) {
            cir.setReturnValue(null);
        }
    }
}
