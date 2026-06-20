package insane96mcp.experiencetweaks.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import insane96mcp.experiencetweaks.module.experience.DroppedExperience;
import insane96mcp.insanelib.core.feature.Feature;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mob.class)
public abstract class MobMixin {
	@Shadow
	protected int xpReward;

	@ModifyReturnValue(method = "getBaseExperienceReward", at = @At(value = "RETURN", ordinal = 0))
	private int experiencetweaks$onXpRewardDrop(int original) {
		if (!Feature.isEnabled(DroppedExperience.class))
			return original;
		return this.xpReward;
	}
}
