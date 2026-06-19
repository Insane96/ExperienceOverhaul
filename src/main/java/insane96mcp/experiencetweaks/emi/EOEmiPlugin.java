package insane96mcp.experiencetweaks.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import insane96mcp.experiencetweaks.module.anvil.anvilrepair.AnvilBetterRepair;
import insane96mcp.experiencetweaks.module.anvil.anvilrepair.AnvilRepair;
import insane96mcp.experiencetweaks.module.anvil.anvilrepair.AnvilRepairReloadListener;
import insane96mcp.insanelib.core.feature.Feature;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

@EmiEntrypoint
public class EOEmiPlugin implements EmiPlugin {
	@Override
	public void register(EmiRegistry registry) {
		if (Feature.isEnabled(AnvilBetterRepair.class)) {
			for (Map.Entry<ResourceLocation, AnvilRepair> anvilRepair : AnvilRepairReloadListener.REPAIRS.entrySet()) {
				for (ItemStack stack : anvilRepair.getValue().itemToRepair.getAllObjects().stream().map(ItemStack::new).toList()) {
					String itemPath = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
					for (AnvilRepair.RepairData repairData : anvilRepair.getValue().repairData) {
						String materialStr = repairData.repairMaterial().toSerializedString();
						String materialPath = materialStr.startsWith("#")
								? "tag_" + ResourceLocation.parse(materialStr.substring(1)).getPath()
								: ResourceLocation.parse(materialStr).getPath();
						ResourceLocation id = ResourceLocation.tryParse(anvilRepair.getKey().getNamespace() + ":/" + itemPath + "_" + materialPath);
						registry.addRecipe(new EmiAnvilRepairRecipe(id, stack, repairData));
					}
				}
			}
			registry.removeRecipes(recipe -> recipe.getId() != null && "emi".equals(recipe.getId().getNamespace()) && recipe.getId().getPath().startsWith("/anvil/repairing/material"));
			registry.removeRecipes(recipe -> recipe.getId() != null && "emi".equals(recipe.getId().getNamespace()) && recipe.getId().getPath().startsWith("/anvil/enchanting"));
		}
	}
}
