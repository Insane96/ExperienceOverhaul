package insane96mcp.experienceoverhaul.mixin.client;

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
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
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

    @ModifyConstant(method = "renderLabels", constant = @Constant(intValue = 40))
    private int experienceoverhaul$tooExpensiveCap(int cap) {
        if (!Feature.isEnabled(AnvilXpCost.class)) return cap;
        return AnvilXpCost.repairCap;
    }

    @Inject(method = "renderLabels", at = @At("TAIL"))
    public void experienceoverhaul$renderFreeLabel(GuiGraphics guiGraphics, int mouseX, int mouseY, CallbackInfo ci) {
        if (this.menu.getCost() != 0 || !this.menu.getSlot(2).hasItem()) return;

        int color = 8453920;
        Component label = Component.translatable("container.repair.free");
        if (!this.menu.getSlot(2).mayPickup(this.player))
            color = 16736352;

        int x = this.imageWidth - 8 - this.font.width(label) - 2;
        int y = 69;
        guiGraphics.fill(x - 2, 67, this.imageWidth - 8, 79, 1325400064);
        guiGraphics.drawString(this.font, label, x, y, color);
    }
}
