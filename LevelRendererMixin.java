package dev.redstonetweaks.mixin;

import dev.redstonetweaks.RenderModule;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TripWireBlock;
import net.minecraft.world.level.block.TripWireHookBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * When a tripwire / tripwire hook only changes its POWERED state, don't queue a chunk
 * section rebuild. The block still updates for real (redstone, collisions, etc.), the
 * client just doesn't redraw the "triggered" look. This saves chunk rebuilds on
 * tripwire-heavy farms.
 */
@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Inject(method = "blockChanged", at = @At("HEAD"), cancellable = true)
    private void lr$skipTripwire(BlockGetter level, BlockPos pos, BlockState oldState, BlockState newState,
                                 int flags, CallbackInfo ci) {
        if (!RenderModule.TRIPWIRE.enabled) return;
        Block block = newState.getBlock();
        if (block != oldState.getBlock()) return;
        if ((block instanceof TripWireBlock || block instanceof TripWireHookBlock)
                && oldState.setValue(BlockStateProperties.POWERED, newState.getValue(BlockStateProperties.POWERED)) == newState) {
            ci.cancel();
        }
    }
}
