package insane96mcp.experienceoverhaul.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import insane96mcp.experienceoverhaul.module.anvil.AnvilXpCost;
import insane96mcp.insanelib.core.feature.Feature;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(value = AnvilScreen.class, priority = 1001)
public abstract class AnvilScreenMixin extends ItemCombinerScreen<AnvilMenu> {

    @Final
    @Shadow
    private Player player;

    public AnvilScreenMixin(AnvilMenu menu, Inventory inventory, Component title, ResourceLocation background) {
        super(menu, inventory, title, background);
    }

    @ModifyExpressionValue(method = "renderLabels", at = @At(value = "CONSTANT", args = "intValue=40"))
    private int experienceoverhaul$tooExpensiveCap(int cap) {
        if (!Feature.isEnabled(AnvilXpCost.class)) return cap;
        return AnvilXpCost.repairCap;
    }
}
