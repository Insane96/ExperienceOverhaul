package insane96mcp.experienceoverhaul.network;

import insane96mcp.experienceoverhaul.network.message.AnvilRepairSyncMessage;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NetworkHandler {
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(AnvilRepairSyncMessage.TYPE, AnvilRepairSyncMessage.STREAM_CODEC, AnvilRepairSyncMessage::handle);
    }
}
