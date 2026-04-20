package insane96mcp.experienceoverhaul.module.anvil;

import insane96mcp.experienceoverhaul.ExperienceOverhaul;
import insane96mcp.insanelib.core.feature.Feature;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@EventBusSubscriber(modid = ExperienceOverhaul.MOD_ID)
public class AnvilHandler {

    private static final int COST_RENAME = 1;

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        boolean renamingNoCost = AnvilRenaming.isNoCost();
        if (!Feature.isEnabled(AnvilXpCost.class)
                && !Feature.isEnabled(AnvilMaterialRepair.class)
                && !Feature.isEnabled(AnvilCustomRepairs.class)
                && !renamingNoCost)
            return;

        ItemStack left = event.getLeft();
        if (left.isEmpty()) return;
        if (!EnchantmentHelper.canStoreEnchantments(left)) return;

        Player player = event.getPlayer();
        ItemStack right = event.getRight();
        String itemName = event.getName() != null ? event.getName() : "";

        ItemStack resultStack = left.copy();
        ItemEnchantments leftEnchantments = resultStack.getEnchantments();

        Map<Holder<Enchantment>, Integer> resultEnchantMap = new HashMap<>();
        for (Holder<Enchantment> holder : leftEnchantments.keySet()) {
            resultEnchantMap.put(holder, leftEnchantments.getLevel(holder));
        }

        int mergeCost = 0;
        int baseCost = (int) event.getCost(); // left.getRepairCost() + right.getRepairCost()
        int repairItemCountCost = 0;
        boolean isRenaming = false;
        boolean isMaterialRepair = false;

        if (!right.isEmpty()) {
            boolean isEnchantedBook = right.is(Items.ENCHANTED_BOOK)
                    && !right.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY).isEmpty();

            Optional<AnvilRepair.RepairData> oRepairData = AnvilCustomRepairs.getCustomAnvilRepair(left, right);

            if (resultStack.isDamageableItem() && (resultStack.getItem().isValidRepairItem(resultStack, right) || oRepairData.isPresent())) {
                isMaterialRepair = true;
                if (oRepairData.isPresent()) {
                    // Custom material repair
                    AnvilRepair.RepairData repairData = oRepairData.get();
                    int maxPartialRepairDmg = Mth.ceil(resultStack.getMaxDamage() * (1f - repairData.maxRepair()));
                    float amountRequired = repairData.amountRequired();

                    float increaseMultiplier = AnvilMaterialRepair.getIncreaseMaterialsRequiredWithEnchantments();
                    if (increaseMultiplier > 0f && left.isEnchanted()) {
                        float increase = 0f;
                        for (Holder<Enchantment> holder : leftEnchantments.keySet())
                            increase += increaseMultiplier * leftEnchantments.getLevel(holder);
                        amountRequired *= 1f + increase;
                    }
                    float increaseFlat = AnvilMaterialRepair.getIncreaseMaterialsRequiredWithEnchantmentsFlat();
                    if (increaseFlat > 0f && left.isEnchanted()) {
                        float increase = 0f;
                        for (Holder<Enchantment> holder : leftEnchantments.keySet())
                            increase += increaseFlat * leftEnchantments.getLevel(holder);
                        amountRequired += increase * repairData.costMultiplier();
                    }

                    float repairSteps = Math.min(resultStack.getDamageValue(), resultStack.getMaxDamage() / amountRequired);
                    if (repairSteps <= 0 || resultStack.getDamageValue() <= maxPartialRepairDmg) {
                        event.setOutput(ItemStack.EMPTY);
                        event.setCost(0);
                        return;
                    }
                    float damageValue = resultStack.getDamageValue();
                    for (repairItemCountCost = 0; repairSteps > 0 && repairItemCountCost < right.getCount() && damageValue > maxPartialRepairDmg; ++repairItemCountCost) {
                        damageValue -= repairSteps;
                        ++mergeCost;
                        repairSteps = Math.min(damageValue, resultStack.getMaxDamage() / amountRequired);
                    }
                    resultStack.setDamageValue((int) Math.max(maxPartialRepairDmg, damageValue));
                } else {
                    // Standard material repair
                    float amountRequired = 4f;
                    float increaseMultiplier = AnvilMaterialRepair.getIncreaseMaterialsRequiredWithEnchantments();
                    if (increaseMultiplier > 0f && left.isEnchanted()) {
                        float increase = 0f;
                        for (Holder<Enchantment> holder : leftEnchantments.keySet())
                            increase += increaseMultiplier * leftEnchantments.getLevel(holder);
                        amountRequired *= 1f + increase;
                    }
                    float increaseFlat = AnvilMaterialRepair.getIncreaseMaterialsRequiredWithEnchantmentsFlat();
                    if (increaseFlat > 0f && left.isEnchanted()) {
                        float increase = 0f;
                        for (Holder<Enchantment> holder : leftEnchantments.keySet())
                            increase += increaseFlat * leftEnchantments.getLevel(holder);
                        amountRequired += increase;
                    }
                    int repairSteps = Math.min(resultStack.getDamageValue(), resultStack.getMaxDamage() / (int) amountRequired);
                    if (repairSteps <= 0) {
                        event.setOutput(ItemStack.EMPTY);
                        event.setCost(0);
                        return;
                    }
                    for (repairItemCountCost = 0; repairSteps > 0 && repairItemCountCost < right.getCount(); ++repairItemCountCost) {
                        resultStack.setDamageValue(resultStack.getDamageValue() - repairSteps);
                        ++mergeCost;
                        repairSteps = (int) Math.min(resultStack.getDamageValue(), resultStack.getMaxDamage() / amountRequired);
                    }
                }
            } else {
                // Merging / enchantment book application
                if (!isEnchantedBook && (!resultStack.is(right.getItem()) || !resultStack.isDamageableItem())) {
                    event.setOutput(ItemStack.EMPTY);
                    event.setCost(0);
                    return;
                }

                if (resultStack.isDamageableItem() && !isEnchantedBook
                        && AnvilMaterialRepair.isAllowMergingItems()) {
                    int leftDurabilityLeft = left.getMaxDamage() - left.getDamageValue();
                    float rightDurabilityLeft = right.getMaxDamage() - right.getDamageValue();
                    if (left.isEnchanted() && AnvilMaterialRepair.decreaseRepairAmountWithEnchantments > 0f) {
                        float mult = 1f - AnvilMaterialRepair.decreaseRepairAmountWithEnchantments.floatValue();
                        for (Holder<Enchantment> holder : leftEnchantments.keySet())
                            rightDurabilityLeft *= (float) Math.pow(mult, leftEnchantments.getLevel(holder));
                    }
                    float resultDurabilityLeft = leftDurabilityLeft + rightDurabilityLeft;
                    resultDurabilityLeft += resultDurabilityLeft * (AnvilMaterialRepair.mergingRepairBonus / 100f);
                    int damageValue = Math.max(0, resultStack.getMaxDamage() - (int) resultDurabilityLeft);
                    if (damageValue < resultStack.getDamageValue()) {
                        resultStack.setDamageValue(damageValue);
                        boolean costBasedOffResult = AnvilXpCost.isMergingCostBasedOffResult();
                        if (!costBasedOffResult)
                            mergeCost += 2;
                    }
                }

                ItemEnchantments rightEnchantments = isEnchantedBook
                        ? right.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY)
                        : right.getEnchantments();
                boolean canEnchant = false;
                boolean cannotEnchant = false;

                for (Holder<Enchantment> rightHolder : rightEnchantments.keySet()) {
                    int leftLvl = resultEnchantMap.getOrDefault(rightHolder, 0);
                    int rightLvl = rightEnchantments.getLevel(rightHolder);
                    rightLvl = (leftLvl == rightLvl && rightLvl < rightHolder.value().getMaxLevel()) ? rightLvl + 1 : Math.max(rightLvl, leftLvl);

                    boolean canEnchant2 = rightHolder.value().canEnchant(left);
                    if (player.getAbilities().instabuild || left.is(Items.ENCHANTED_BOOK))
                        canEnchant2 = true;

                    for (Holder<Enchantment> leftHolder : resultEnchantMap.keySet()) {
                        if (!leftHolder.equals(rightHolder) && !Enchantment.areCompatible(rightHolder, leftHolder)) {
                            canEnchant2 = false;
                            if (!AnvilXpCost.isMergingCostBasedOffResult())
                                ++mergeCost;
                        }
                    }

                    if (!canEnchant2) {
                        cannotEnchant = true;
                    } else {
                        canEnchant = true;
                        if (rightLvl > rightHolder.value().getMaxLevel() && leftLvl == rightLvl)
                            rightLvl = rightHolder.value().getMaxLevel();
                        resultEnchantMap.put(rightHolder, rightLvl);

                        int enchantCost = rightHolder.value().getAnvilCost();
                        if (isEnchantedBook)
                            enchantCost = Math.max(1, enchantCost / 2);
                        if (!AnvilXpCost.isMergingCostBasedOffResult())
                            mergeCost += enchantCost * rightLvl;
                    }
                }

                if (AnvilXpCost.isMergingCostBasedOffResult()) {
                    for (Map.Entry<Holder<Enchantment>, Integer> entry : resultEnchantMap.entrySet()) {
                        int enchantCost = entry.getKey().value().getAnvilCost();
                        if (isEnchantedBook)
                            enchantCost = Math.max(1, enchantCost / 2);
                        mergeCost += enchantCost * entry.getValue();
                    }
                }

                // Pure durability merge with no enchantments — no XP cost
                if (!right.isEnchanted() && !right.is(Items.ENCHANTED_BOOK))
                    mergeCost = 0;

                if (cannotEnchant && !canEnchant) {
                    event.setOutput(ItemStack.EMPTY);
                    event.setCost(0);
                    return;
                }
            }
        }

        // Renaming
        if (StringUtils.isBlank(itemName)) {
            if (left.has(DataComponents.CUSTOM_NAME)) {
                isRenaming = true;
                resultStack.remove(DataComponents.CUSTOM_NAME);
            }
        } else if (!itemName.equals(left.getHoverName().getString())) {
            isRenaming = true;
            resultStack.set(DataComponents.CUSTOM_NAME, Component.literal(itemName));
        }

        // Final cost
        double xpPerEnchantLevel = AnvilXpCost.getXpCostPerEnchantmentLevel();
        int finalCost;
        if (isMaterialRepair && xpPerEnchantLevel >= 0) {
            int totalEnchantLevels = 0;
            for (Holder<Enchantment> holder : leftEnchantments.keySet())
                totalEnchantLevels += leftEnchantments.getLevel(holder);
            finalCost = (int) Math.round(totalEnchantLevels * xpPerEnchantLevel);
        } else {
            double multiplier = AnvilXpCost.getMultiplier();
            finalCost = (int) Math.round((baseCost + mergeCost) * multiplier);
        }

        if (isRenaming && !AnvilRenaming.isNoCost())
            finalCost += COST_RENAME;
        if (isRenaming && AnvilRenaming.isNoCost() && mergeCost <= 0)
            finalCost = 0;

        if (!isRenaming && right.isEmpty())
            resultStack = ItemStack.EMPTY;

        int repairCap = AnvilXpCost.getRepairCap();
        if (finalCost >= repairCap && !player.getAbilities().instabuild)
            resultStack = ItemStack.EMPTY;

        // Update repair cost component and enchantments on result
        double multiplier = isMaterialRepair && xpPerEnchantLevel >= 0 ? 0.0 : AnvilXpCost.getMultiplier();
        if (!resultStack.isEmpty()) {
            if (multiplier != 0) {
                int toolRepairCost = resultStack.getOrDefault(DataComponents.REPAIR_COST, 0);
                if (!right.isEmpty() && toolRepairCost < right.getOrDefault(DataComponents.REPAIR_COST, 0))
                    toolRepairCost = right.getOrDefault(DataComponents.REPAIR_COST, 0);
                if (mergeCost >= 1)
                    toolRepairCost = AnvilMenu.calculateIncreasedRepairCost(toolRepairCost);
                resultStack.set(DataComponents.REPAIR_COST, toolRepairCost);
            }

            ItemEnchantments.Mutable mutableEnchants = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
            for (Map.Entry<Holder<Enchantment>, Integer> e : resultEnchantMap.entrySet()) {
                mutableEnchants.set(e.getKey(), e.getValue());
            }
            EnchantmentHelper.setEnchantments(resultStack, mutableEnchants.toImmutable());
        }

        event.setOutput(resultStack);
        event.setCost(finalCost);
        event.setMaterialCost(repairItemCountCost);
    }
}
