package insane96mcp.experienceoverhaul.module.anvil;

import insane96mcp.experienceoverhaul.module.EOModules;
import insane96mcp.insanelib.core.feature.Feature;
import insane96mcp.insanelib.core.feature.LoadFeature;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

@LoadFeature(module = EOModules.ANVIL, description = "Allows custom anvil repair recipes defined via datapacks in data/<modid>/anvil_repairs/*.json.")
public class AnvilCustomRepairs extends Feature {

    public static Optional<AnvilRepair> getCustomAnvilRepair(ItemStack left) {
        if (!Feature.isEnabled(AnvilCustomRepairs.class))
            return Optional.empty();
        for (AnvilRepair repair : AnvilRepairReloadListener.REPAIRS.values()) {
            if (repair.isItemToRepair(left))
                return Optional.of(repair);
        }
        return Optional.empty();
    }

    public static Optional<AnvilRepair.RepairData> getCustomAnvilRepair(ItemStack left, ItemStack right) {
        if (!Feature.isEnabled(AnvilCustomRepairs.class))
            return Optional.empty();
        for (AnvilRepair repair : AnvilRepairReloadListener.REPAIRS.values()) {
            if (repair.isItemToRepair(left)) {
                Optional<AnvilRepair.RepairData> data = repair.getRepairDataFromMaterial(right);
                if (data.isPresent())
                    return data;
            }
        }
        return Optional.empty();
    }
}
