package com.ultreon.devices.network;

import com.ultreon.devices.Devices;
import com.ultreon.devices.network.task.*;
import dev.architectury.networking.NetworkChannel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class PacketHandler {
    public static final NetworkChannel INSTANCE = NetworkChannel.create(Devices.id("main_channel"));
    private static int id = 0;

    public static void init() {
        INSTANCE.register(RequestPacket.class, RequestPacket::toBytes, RequestPacket::new, RequestPacket::onMessage);
        INSTANCE.register(ResponsePacket.class, Packet::toBytes, ResponsePacket::new, ResponsePacket::onMessage);
        INSTANCE.register(SyncApplicationPacket.class, Packet::toBytes, SyncApplicationPacket::new, SyncApplicationPacket::onMessage);
        INSTANCE.register(SyncConfigPacket.class, Packet::toBytes, SyncConfigPacket::new, SyncConfigPacket::onMessage);
        INSTANCE.register(SyncBlockPacket.class, Packet::toBytes, SyncBlockPacket::new, SyncBlockPacket::onMessage);
        INSTANCE.register(NotificationPacket.class, Packet::toBytes, NotificationPacket::new, NotificationPacket::onMessage);
    }

    private static int nextId() {
        return id++;
    }

    public static <T extends Packet<T>> void sendToClient(Packet<T> messageNotification, ServerPlayer player) {
        INSTANCE.sendToPlayer(player, messageNotification);
        //INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), messageNotification);
    }

    // seems to be unused
    public static <T extends Packet<T>> void sendToDimension(Packet<T> messageNotification, ResourceKey<Level> level) {
        //INSTANCE.sendToServer();
        //INSTANCE.send(PacketDistributor.DIMENSION.with(() -> level), messageNotification);
    }

//    public static <T extends Packet<T>> void sendToDimension(Packet<T> messageNotification, Level level) {
//        INSTANCE.send(PacketDistributor.DIMENSION.with(level::dimension), messageNotification);
//    }
//
//    public static <T extends Packet<T>> void sendToServer(Packet<T> messageNotification) {
//        INSTANCE.send(PacketDistributor.SERVER.noArg(), messageNotification);
//    }
//
//    public static <T extends Packet<T>> void sendToAll(Packet<T> messageNotification) {
//        INSTANCE.send(PacketDistributor.ALL.noArg(), messageNotification);
//    }
//
//    public static <T extends Packet<T>> void sendToAllAround(Packet<T> messageNotification, ResourceKey<Level> level, double x, double y, double z, double radius) {
//        INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(x, y, z, radius, level)), messageNotification);
//    }
//
//    public static <T extends Packet<T>> void sendToAllAround(Packet<T> messageNotification, Level level, double x, double y, double z, double radius) {
//        INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(x, y, z, radius, level.dimension())), messageNotification);
//    }
//
//    public static <T extends Packet<T>> void sendToTrackingChunk(Packet<T> messageNotification, LevelChunk chunk) {
//        INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), messageNotification);
//    }
//
//    public static <T extends Packet<T>> void sendToTrackingChunk(Packet<T> messageNotification, Level level, int x, int z) {
//        INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunk(x, z)), messageNotification);
//    }
//
//    public static <T extends Packet<T>> void sendToTrackingEntity(Packet<T> messageNotification, Entity entity) {
//        INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), messageNotification);
//    }
//
//    public static <T extends Packet<T>> void sendToTrackingEntity(Packet<T> messageNotification, Level level, int entityId) {
//        INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> level.getEntity(entityId)), messageNotification);
//    }
//
//    public static <T extends Packet<T>> void sendToTrackingEntityAndSelf(Packet<T> messageNotification, Entity entity) {
//        INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), messageNotification);
//    }
//
//    public static <T extends Packet<T>> void sendToTrackingEntityAndSelf(Packet<T> messageNotification, Level level, int entityId) {
//        INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> level.getEntity(entityId)), messageNotification);
//    }
}
