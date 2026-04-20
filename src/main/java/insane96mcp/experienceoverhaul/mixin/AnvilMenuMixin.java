package insane96mcp.experienceoverhaul.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AnvilMenu.class, priority = 1001)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {

    @Shadow
    @Final
    private static int COST_RENAME;

    @Shadow
    private DataSlot cost;

    public AnvilMenuMixin(@Nullable MenuType<?> type, int containerId, net.minecraft.world.entity.player.Inventory inventory, net.minecraft.world.inventory.ContainerLevelAccess access) {
        super(type, containerId, inventory, access);
    }

    /**
     * Removes the vanilla cost >= 1 guard so cost-0 results (free rename, multiplier=0) are clickable.
     */
    @Inject(method = "mayPickup", at = @At("HEAD"), cancellable = true)
    private void experienceoverhaul$mayPickup(Player player, boolean unused, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(player.getAbilities().instabuild || player.experienceLevel >= this.cost.get());
    }
}
