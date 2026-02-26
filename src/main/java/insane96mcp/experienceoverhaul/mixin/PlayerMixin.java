package insane96mcp.experienceoverhaul.mixin;

import insane96mcp.experienceoverhaul.module.experience.PlayerExperience;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin {
    @Shadow
    public int experienceLevel;

    @Inject(at = @At("RETURN"), method = "getXpNeededForNextLevel", cancellable = true)
    private void experienceoverhaul$xpForNextLevel(CallbackInfoReturnable<Integer> callback) {
        int exp = PlayerExperience.getBetterScalingLevel(this.experienceLevel);
        if (exp != -1)
            callback.setReturnValue(exp);
    }

    @Inject(at = @At("HEAD"), method = "getBaseExperienceReward", cancellable = true)
    private void experienceoverhaul$getExperiencePoints(CallbackInfoReturnable<Integer> callback) {
        int exp = PlayerExperience.getExperienceOnDeath((Player) (Object) this, false);
        if (exp != -1)
            callback.setReturnValue(exp);
    }
}
