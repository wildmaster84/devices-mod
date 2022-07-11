package com.ultreon.devices.event;

import com.ultreon.devices.DevicesMod;
import com.ultreon.devices.api.WorldSavedData;
import com.ultreon.devices.api.utils.BankUtil;
import com.ultreon.devices.programs.email.EmailManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

public class WorldDataHandler {
    private static final LevelResource DEVICES_MOD_DATA = new LevelResource("data/devices-mod");

    @SubscribeEvent
    public void load(final ServerStartingEvent event) {
        final File modData = Objects.requireNonNull(event.getServer(), "World loaded without server").getWorldPath(DEVICES_MOD_DATA).toFile();
        if (!modData.exists()) {
            try {
                Files.createDirectories(modData.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        loadData(modData, "emails.dat", EmailManager.INSTANCE);
        loadData(modData, "bank.dat", BankUtil.INSTANCE);
    }

    @SubscribeEvent
    public void save(final WorldEvent.Save event) {
        final MinecraftServer server = event.getWorld().getServer();
        if (server == null) {
            if (event.getWorld().getLevelData() instanceof ServerLevelData serverLevelData)
                DevicesMod.LOGGER.warn("World {} saved without server", serverLevelData.getLevelName());
            else
                DevicesMod.LOGGER.warn("World saved without server");
            return;
        }
        File modData = server.getWorldPath(DEVICES_MOD_DATA).toFile();
        if (!modData.exists()) {
            try {
                Files.createDirectories(modData.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        saveData(modData, "emails.dat", EmailManager.INSTANCE);
        saveData(modData, "bank.dat", BankUtil.INSTANCE);
    }

    private void loadData(File modData, String fileName, WorldSavedData data) {
        File dataFile = new File(modData, fileName);
        if (!dataFile.exists()) {
            return;
        }
        try {
            CompoundTag nbt = NbtIo.readCompressed(dataFile);
            data.load(nbt);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveData(File modData, String fileName, WorldSavedData data) {
        try {
            File dataFile = new File(modData, fileName);
            if (!dataFile.exists()) {
                Files.createFile(dataFile.toPath());
            }

            CompoundTag nbt = new CompoundTag();
            data.save(nbt);
            NbtIo.writeCompressed(nbt, dataFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
