package insane96mcp.experienceoverhaul.module.anvil.anvilrepair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import insane96mcp.experienceoverhaul.ExperienceOverhaul;
import insane96mcp.experienceoverhaul.network.message.AnvilRepairSyncMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = ExperienceOverhaul.MOD_ID)
public class AnvilRepairReloadListener extends SimpleJsonResourceReloadListener {
    public static HashMap<ResourceLocation, AnvilRepair> REPAIRS = new HashMap<>();
    public static final AnvilRepairReloadListener INSTANCE;
    private static final Gson GSON = new GsonBuilder().create();

    static {
        INSTANCE = new AnvilRepairReloadListener();
    }

    public AnvilRepairReloadListener() {
        super(GSON, "anvil_repairs");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        REPAIRS.clear();
        for (var entry : map.entrySet()) {
            try {
                AnvilRepair anvilRepair = GSON.fromJson(entry.getValue(), AnvilRepair.class);
                REPAIRS.put(entry.getKey(), anvilRepair);
            }
            catch (JsonSyntaxException e) {
                ExperienceOverhaul.LOGGER.error("Parsing error loading Anvil Repair {}: {}", entry.getKey(), e.getMessage());
            }
            catch (Exception e) {
                ExperienceOverhaul.LOGGER.error("Failed loading Anvil Repair {}: {}", entry.getKey(), e.getMessage());
            }
        }
        ExperienceOverhaul.LOGGER.info("Loaded {} Anvil Repairs", REPAIRS.size());
    }

    @SubscribeEvent
    public static void onDataPackSync(OnDatapackSyncEvent event) {
        if (event.getPlayer() == null) {
            event.getPlayerList().getPlayers().forEach(player -> AnvilRepairSyncMessage.sync(REPAIRS, player));
        }
        else {
            AnvilRepairSyncMessage.sync(REPAIRS, event.getPlayer());
        }
    }

    @Override
    public String getName() {
        return "Anvil Repair Reload Listener";
    }
}
