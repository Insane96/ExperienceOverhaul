package insane96mcp.experienceoverhaul;

import com.mojang.logging.LogUtils;
import insane96mcp.experienceoverhaul.module.EOModules;
import insane96mcp.experienceoverhaul.network.NetworkHandler;
import insane96mcp.insanelib.setup.ILModConfig;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(ExperienceOverhaul.MOD_ID)
public class ExperienceOverhaul {
    public static final String MOD_ID = "experienceoverhaul";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ILModConfig CONFIG;

    public ExperienceOverhaul(IEventBus eventBus, ModContainer modContainer) {
        CONFIG = new ILModConfig(MOD_ID, ModConfig.Type.COMMON, eventBus, EOModules::init, ExperienceOverhaul.class.getClassLoader());
        modContainer.registerConfig(ModConfig.Type.COMMON, CONFIG.spec);

        eventBus.addListener(NetworkHandler::register);
    }

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
