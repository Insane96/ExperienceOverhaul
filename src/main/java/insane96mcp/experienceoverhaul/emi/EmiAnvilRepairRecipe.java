package insane96mcp.experienceoverhaul.emi;

import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import insane96mcp.experienceoverhaul.module.anvil.anvilrepair.AnvilRepair;
import insane96mcp.insanelib.data.ObjTag;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class EmiAnvilRepairRecipe extends BasicEmiRecipe {

	public EmiAnvilRepairRecipe(ResourceLocation id, ItemStack itemToRepair, AnvilRepair.RepairData repairData) {
		super(VanillaEmiRecipeCategories.ANVIL_REPAIRING, id, 125, 18);
		ItemStack item = itemToRepair.copy();
		item.setDamageValue(itemToRepair.getMaxDamage() - 1);
		this.inputs.add(EmiStack.of(item));
		this.inputs.add(EmiIngredient.of(ingredientFromObjTag(repairData.repairMaterial()), 1));
		item = itemToRepair.copy();
		item.setDamageValue(item.getMaxDamage() - (int) (item.getMaxDamage() / repairData.amountRequired()));
		this.outputs.add(EmiStack.of(item));
	}

	static Ingredient ingredientFromObjTag(ObjTag<Item> objTag) {
		Holder<Item> holder = objTag.asHolder();
		if (holder != null)
			return Ingredient.of(holder.value());
		String str = objTag.toSerializedString();
		return Ingredient.of(TagKey.create(Registries.ITEM, ResourceLocation.parse(str.substring(1))));
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.addTexture(EmiTexture.PLUS, 27, 3);
		widgets.addTexture(EmiTexture.EMPTY_ARROW, 75, 1);
		widgets.addSlot(inputs.get(0), 0, 0);
		widgets.addSlot(inputs.get(1), 49, 0);
		widgets.addSlot(outputs.getFirst(), 107, 0).recipeContext(this);
	}
}
