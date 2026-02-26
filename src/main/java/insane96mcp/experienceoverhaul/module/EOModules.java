package insane96mcp.experienceoverhaul.module;

import insane96mcp.experienceoverhaul.ExperienceOverhaul;
import insane96mcp.insanelib.core.feature.Module;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class EOModules {
	public static final String EXPERIENCE = ExperienceOverhaul.MOD_ID + ":experience";
	public static final String ANVIL      = ExperienceOverhaul.MOD_ID + ":anvil";
	public static final String ENCHANTING = ExperienceOverhaul.MOD_ID + ":enchanting";

	public static void init(IEventBus eventBus, ModConfigSpec.Builder builder) {
		create(EXPERIENCE, "Experience", eventBus, builder);
		create(ANVIL, "Anvil", eventBus, builder);
		create(ENCHANTING, "Enchanting", eventBus, builder);
	}

	public static void create(String id, String name, IEventBus eventBus, ModConfigSpec.Builder builder) {
		Module.Builder.create(ResourceLocation.parse(id), name, ModConfig.Type.COMMON, builder, eventBus).build();
	}
}
