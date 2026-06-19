package insane96mcp.experiencetweaks.module.anvil;

import insane96mcp.experiencetweaks.module.EOModules;
import insane96mcp.insanelib.core.feature.Feature;
import insane96mcp.insanelib.core.feature.LoadFeature;
import insane96mcp.insanelib.core.feature.config.Config;

@LoadFeature(module = EOModules.ANVIL, description = "Modifies how materials and item merging work when repairing items in an anvil.")
public class AnvilMaterialRepair extends Feature {

	@Config(min = 0d, description = "Percentage increase in materials required to repair an item for each enchantment level on the item.")
	public static Double increaseMaterialsRequiredWithEnchantments = 0.0d;
	@Config(min = 0d, description = "Flat increase in materials required to repair an item for each enchantment level on the item.")
	public static Double increaseMaterialsRequiredWithEnchantmentsFlat = 0.0d;
	@Config(min = 0d, description = "Multiplicative percentage decrease to the repair amount for each enchantment level on the item when merging two items.")
	public static Double decreaseRepairAmountWithEnchantments = 0.05d;
	@Config(description = "If true, two items of the same type can be merged in the anvil for a repair bonus.")
	public static Boolean allowMergingItems = true;
	@Config(min = 0, max = 100, description = "Bonus repair percentage when merging two items of the same type. Vanilla is 12%.")
	public static Integer mergingRepairBonus = 10;

	public static float getIncreaseMaterialsRequiredWithEnchantments() {
		return Feature.isEnabled(AnvilMaterialRepair.class) ? increaseMaterialsRequiredWithEnchantments.floatValue() : 0f;
	}

	public static float getIncreaseMaterialsRequiredWithEnchantmentsFlat() {
		return Feature.isEnabled(AnvilMaterialRepair.class) ? increaseMaterialsRequiredWithEnchantmentsFlat.floatValue() : 0f;
	}

	public static boolean isAllowMergingItems() {
		return Feature.isEnabled(AnvilMaterialRepair.class) && allowMergingItems;
	}
}
