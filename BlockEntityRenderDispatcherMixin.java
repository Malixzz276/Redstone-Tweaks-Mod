package dev.redstonetweaks.mixin;

import dev.redstonetweaks.RenderModule;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Moving piston blocks / extending heads have no renderer -> nothing drawn. Server logic untouched. */
@Mixin(BlockEntityRenderDispatcher.class)
public class BlockEntityRenderDispatcherMixin {
    @Inject(method = "getRenderer", at = @At("HEAD"), cancellable = true)
    private void lr$skipPiston(BlockEntity blockEntity, CallbackInfoReturnable<BlockEntityRenderer<?, ?>> cir) {
        if (RenderModule.PISTON.enabled && blockEntity instanceof PistonMovingBlockEntity) {
            cir.setReturnValue(null);
        }
    }
}
