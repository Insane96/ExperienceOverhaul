package insane96mcp.experienceoverhaul.network.message;

import insane96mcp.experienceoverhaul.ExperienceOverhaul;
import insane96mcp.experienceoverhaul.module.experience.DroppedExperience;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncExperienceDisabledGameruleMessage(boolean disabled) implements CustomPacketPayload {
    public static final Type<SyncExperienceDisabledGameruleMessage> TYPE =
            new Type<>(ExperienceOverhaul.location("sync_experience_disabled_gamerule"));

    public static final StreamCodec<ByteBuf, SyncExperienceDisabledGameruleMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, SyncExperienceDisabledGameruleMessage::disabled,
            SyncExperienceDisabledGameruleMessage::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final SyncExperienceDisabledGameruleMessage payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            DroppedExperience.disableExperience = payload.disabled;
        });
    }

    public static void sync(boolean disabled) {
        var msg = new SyncExperienceDisabledGameruleMessage(disabled);
        PacketDistributor.sendToAllPlayers(msg);
    }

    public static void sync(ServerPlayer player, boolean disabled) {
        var msg = new SyncExperienceDisabledGameruleMessage(disabled);
        PacketDistributor.sendToPlayer(player, msg);
    }
}