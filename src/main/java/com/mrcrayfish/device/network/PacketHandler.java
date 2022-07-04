package com.mrcrayfish.device.network;

import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.network.task.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(MrCrayfishDeviceMod.res("main_channel"), () -> Reference.VERSION, it -> it.equals(Reference.VERSION), it -> it.equals(Reference.VERSION));
    private static int id = 0;

    public static void init() {
        INSTANCE.registerMessage(nextId(), RequestPacket.class, Packet::toBytes, RequestPacket::new, RequestPacket::onMessage);
        INSTANCE.registerMessage(nextId(), ResponsePacket.class, Packet::toBytes, ResponsePacket::new, ResponsePacket::onMessage);
        INSTANCE.registerMessage(nextId(), SyncApplicationPacket.class, Packet::toBytes, SyncApplicationPacket::new, SyncApplicationPacket::onMessage);
        INSTANCE.registerMessage(nextId(), SyncConfigPacket.class, Packet::toBytes, SyncConfigPacket::new, SyncConfigPacket::onMessage);
        INSTANCE.registerMessage(nextId(), SyncBlockPacket.class, Packet::toBytes, SyncBlockPacket::new, SyncBlockPacket::onMessage);
        INSTANCE.registerMessage(nextId(), NotificationPacket.class, Packet::toBytes, NotificationPacket::new, NotificationPacket::onMessage);
    }

    private static int nextId() {
        return id++;
    }

    public static <T extends Packet<T>> void sendToClient(Packet<T> messageNotification, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), messageNotification);
    }

    public static <T extends Packet<T>> void sendToDimension(Packet<T> messageNotification, ResourceKey<Level> level) {
        INSTANCE.send(PacketDistributor.DIMENSION.with(() -> level), messageNotification);
    }

    public static <T extends Packet<T>> void sendToDimension(Packet<T> messageNotification, Level level) {
        INSTANCE.send(PacketDistributor.DIMENSION.with(level::dimension), messageNotification);
    }

    public static <T extends Packet<T>> void sendToServer(Packet<T> messageNotification) {
        INSTANCE.send(PacketDistributor.SERVER.noArg(), messageNotification);
    }

    public static <T extends Packet<T>> void sendToAll(Packet<T> messageNotification) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), messageNotification);
    }

    public static <T extends Packet<T>> void sendToAllAround(Packet<T> messageNotification, ResourceKey<Level> level, double x, double y, double z, double radius) {
        INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(x, y, z, radius, level)), messageNotification);
    }

    public static <T extends Packet<T>> void sendToAllAround(Packet<T> messageNotification, Level level, double x, double y, double z, double radius) {
        INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(x, y, z, radius, level.dimension())), messageNotification);
    }

    public static <T extends Packet<T>> void sendToTrackingChunk(Packet<T> messageNotification, LevelChunk chunk) {
        INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), messageNotification);
    }

    public static <T extends Packet<T>> void sendToTrackingChunk(Packet<T> messageNotification, Level level, int x, int z) {
        INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunk(x, z)), messageNotification);
    }

    public static <T extends Packet<T>> void sendToTrackingEntity(Packet<T> messageNotification, Entity entity) {
        INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), messageNotification);
    }

    public static <T extends Packet<T>> void sendToTrackingEntity(Packet<T> messageNotification, Level level, int entityId) {
        INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> level.getEntity(entityId)), messageNotification);
    }

    public static <T extends Packet<T>> void sendToTrackingEntityAndSelf(Packet<T> messageNotification, Entity entity) {
        INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), messageNotification);
    }

    public static <T extends Packet<T>> void sendToTrackingEntityAndSelf(Packet<T> messageNotification, Level level, int entityId) {
        INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> level.getEntity(entityId)), messageNotification);
    }
}
