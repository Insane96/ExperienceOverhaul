package insane96mcp.experienceoverhaul.module.anvil;

import insane96mcp.experienceoverhaul.module.EOModules;
import insane96mcp.insanelib.core.feature.Feature;
import insane96mcp.insanelib.core.feature.LoadFeature;
import insane96mcp.insanelib.core.feature.config.Config;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.AnvilRepairEvent;

@LoadFeature(module = EOModules.ANVIL, description = "Removes the XP cost for renaming items and prevents anvil degradation when only renaming.")
public class AnvilRenaming extends Feature {

	@Config(description = "Renaming items in an anvil doesn't cost XP")
	public static Boolean noCost = true;
	@Config(description = "Anvils don't wear down when only used for renaming")
	public static Boolean noBreak = true;

	public static boolean isNoCost() {
		return Feature.isEnabled(AnvilRenaming.class) && noCost;
	}

	@SubscribeEvent
	public void onAnvilRepair(AnvilRepairEvent event) {
		if (!this.isEnabled() || !noBreak)
			return;
		if (event.getRight().isEmpty() && ItemStack.isSameItem(event.getLeft(), event.getOutput()))
			event.setBreakChance(0);
	}
}
