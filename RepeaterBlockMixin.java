package dev.redstonetweaks.mixin;

import dev.redstonetweaks.RenderModule;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Skips the client-only ambient particle effect; redstone behavior is unchanged. */
@Mixin(RepeaterBlock.class)
public class RepeaterBlockMixin {
    @Inject(method = "animateTick", at = @At("HEAD"), cancellable = true)
    private void lr$skip(BlockState state, Level level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (RenderModule.REPEATER_COMPARATOR.enabled) ci.cancel();
    }
}
