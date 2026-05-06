package farn.matmos.mixin;

import matmos.minecraft.MAtmos;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Shadow
    private Minecraft client;

    @Inject(method="onFrameUpdate", at = @At("TAIL"))
    public void renderFrameWhatever(float fl, CallbackInfo ci) {
        if(client.world != null)
            MAtmos.OnTickInGame();
    }
}
