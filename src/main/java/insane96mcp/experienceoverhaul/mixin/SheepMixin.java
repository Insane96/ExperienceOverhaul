package insane96mcp.experienceoverhaul.mixin;

import insane96mcp.experienceoverhaul.module.experience.DroppedExperience;
import insane96mcp.insanelib.core.feature.Feature;
import insane96mcp.insanelib.util.MathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Sheep.class)
public abstract class SheepMixin extends Animal {
	protected SheepMixin(EntityType<? extends Animal> pEntityType, Level pLevel) {
		super(pEntityType, pLevel);
	}

	@Inject(method = "shear", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Sheep;setSheared(Z)V"))
	private void experienceoverhaul$xpOnShear(SoundSource category, CallbackInfo ci) {
		if (!Feature.isEnabled(DroppedExperience.class))
			return;

		level().addFreshEntity(new ExperienceOrb(level(), this.getX(), this.getY(), this.getZ(), DroppedExperience.shearXp.getIntRandBetween(level().random)));
	}
}
