package com.mrcrayfish.device;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import com.mrcrayfish.device.api.ApplicationManager;
import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.print.IPrint;
import com.mrcrayfish.device.api.print.PrintingManager;
import com.mrcrayfish.device.api.task.TaskManager;
import com.mrcrayfish.device.block.PrinterBlock;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.core.client.ClientNotification;
import com.mrcrayfish.device.core.io.task.*;
import com.mrcrayfish.device.core.network.task.TaskConnect;
import com.mrcrayfish.device.core.network.task.TaskGetDevices;
import com.mrcrayfish.device.core.network.task.TaskPing;
import com.mrcrayfish.device.core.print.task.TaskPrint;
import com.mrcrayfish.device.init.RegistrationHandler;
import com.mrcrayfish.device.network.PacketHandler;
import com.mrcrayfish.device.network.task.SyncApplicationPacket;
import com.mrcrayfish.device.network.task.SyncConfigPacket;
import com.mrcrayfish.device.object.AppInfo;
import com.mrcrayfish.device.programs.ApplicationIcons;
import com.mrcrayfish.device.programs.gitweb.ApplicationGitWeb;
import com.mrcrayfish.device.programs.system.*;
import com.mrcrayfish.device.programs.system.task.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.loading.DatagenModLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Reference.MOD_ID)
public class MrCrayfishDeviceMod implements PreparableReloadListener {
    private static MrCrayfishDeviceMod instance;

    public static final CreativeModeTab TAB_DEVICE = new DeviceTab("cdm_tab_device");

    public static final Logger LOGGER = LogUtils.getLogger();

    public static final boolean DEVELOPER_MODE = true;

    List<AppInfo> allowedApps;

    public MrCrayfishDeviceMod() throws LaunchException {
        if (DEVELOPER_MODE && FMLEnvironment.production) {
            throw new LaunchException();
        }

        instance = this;

        DeviceConfig.init();

        FMLJavaModLoadingContext javaFmlLoadingCtx = FMLJavaModLoadingContext.get();
        ModLoadingContext loadingCtx = ModLoadingContext.get();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
        IEventBus modEventBus = javaFmlLoadingCtx.getModEventBus();

        // Common side stuff
        forgeEventBus.register(this);

        LOGGER.info("Registering common setup handler, and load complete handler.");

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::loadComplete);

        LOGGER.info("Initializing registration handler and mod config.");
        RegistrationHandler.register(modEventBus);
        DeviceConfig.register(loadingCtx);

        // Client side stuff
        if (!DatagenModLoader.isRunningDataGen()) {
            LOGGER.info("Registering the reload listener.");
//            ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener(this);
        }

        // Server side stuff
        LOGGER.info("Registering server setup handler.");
        modEventBus.addListener(this::serverSetup);

        // IMC stuff
        LOGGER.info("Registering IMC handlers.");
        javaFmlLoadingCtx.getModEventBus().addListener(this::enqueueIMC);
        javaFmlLoadingCtx.getModEventBus().addListener(this::processIMC);

        // Register ourselves for server and other game events we are interested in
        LOGGER.info("Registering mod class to forge events.");
        forgeEventBus.register(this);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());

        LOGGER.info("Doing some common setup.");

        PacketHandler.init();

        registerApplications();
    }

    private void serverSetup(FMLDedicatedServerSetupEvent event) {
        LOGGER.info("Doing some server setup.");
    }

    public void loadComplete(FMLLoadCompleteEvent event) {
        LOGGER.info("Doing some load complete handling.");
        generateIconAtlas();
    }


    private void registerApplications() {
        // Applications (Both)
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "settings"), ApplicationSettings.class);
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "bank"), ApplicationBank.class);
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "file_browser"), ApplicationFileBrowser.class);
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "gitweb"), ApplicationGitWeb.class);
//        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "note_stash"), ApplicationNoteStash.class);
//        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "pixel_painter"), ApplicationPixelPainter.class);
//        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "ender_mail"), ApplicationEmail.class);
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "app_store"), ApplicationAppStore.class);

        // Core
        TaskManager.registerTask(TaskUpdateApplicationData.class);
        TaskManager.registerTask(TaskPrint.class);
        TaskManager.registerTask(TaskUpdateSystemData.class);
        TaskManager.registerTask(TaskConnect.class);
        TaskManager.registerTask(TaskPing.class);
        TaskManager.registerTask(TaskGetDevices.class);

        //Bank
        TaskManager.registerTask(TaskDeposit.class);
        TaskManager.registerTask(TaskWithdraw.class);
        TaskManager.registerTask(TaskGetBalance.class);
        TaskManager.registerTask(TaskPay.class);
        TaskManager.registerTask(TaskAdd.class);
        TaskManager.registerTask(TaskRemove.class);

        //File browser
        TaskManager.registerTask(TaskSendAction.class);
        TaskManager.registerTask(TaskSetupFileBrowser.class);
        TaskManager.registerTask(TaskGetFiles.class);
        TaskManager.registerTask(TaskGetStructure.class);
        TaskManager.registerTask(TaskGetMainDrive.class);

        // Todo implement ender mail
//        //Ender Mail
//        TaskManager.registerTask(TaskUpdateInbox.class);
//        TaskManager.registerTask(TaskSendEmail.class);
//        TaskManager.registerTask(TaskCheckEmailAccount.class);
//        TaskManager.registerTask(TaskRegisterEmailAccount.class);
//        TaskManager.registerTask(TaskDeleteEmail.class);
//        TaskManager.registerTask(TaskViewEmail.class);

        if (!DEVELOPER_MODE) {
            // Applications (Normal)
            //ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "boat_racers"), ApplicationBoatRacers.class);
            //ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "mine_bay"), ApplicationMineBay.class);

            // Tasks (Normal)
            //TaskManager.registerTask(TaskAddAuction.class);
            //TaskManager.registerTask(TaskGetAuctions.class);
            //TaskManager.registerTask(TaskBuyItem.class);
        } else {
            // Applications (Developers)
//            ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "example"), ApplicationExample.class);
            ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "icons"), ApplicationIcons.class);
//            ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "text_area"), ApplicationTextArea.class);
//            ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "test"), ApplicationTest.class);

//            TaskManager.registerTask(TaskNotificationTest.class);
        }

//        PrintingManager.registerPrint(new ResourceLocation(Reference.MOD_ID, "picture"), ApplicationPixelPainter.PicturePrint.class);
    }

    private void generateIconAtlas() {
        final int ICON_SIZE = 14;
        int index = 0;

        BufferedImage atlas = new BufferedImage(ICON_SIZE * 16, ICON_SIZE * 16, BufferedImage.TYPE_INT_ARGB);
        Graphics g = atlas.createGraphics();

        try {
            BufferedImage icon = ImageIO.read(Objects.requireNonNull(MrCrayfishDeviceMod.class.getResourceAsStream("/assets/" + Reference.MOD_ID + "/textures/app/icon/missing.png")));
            g.drawImage(icon, 0, 0, ICON_SIZE, ICON_SIZE, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        index++;

        for (AppInfo info : ApplicationManager.getAllApplications()) {
            if (info.getIcon() == null) continue;

            ResourceLocation identifier = info.getId();
            ResourceLocation iconResource = new ResourceLocation(info.getIcon());
            String path = "/assets/" + iconResource.getNamespace() + "/" + iconResource.getPath();
            try {
                InputStream input = MrCrayfishDeviceMod.class.getResourceAsStream(path);
                if (input != null) {
                    BufferedImage icon = ImageIO.read(input);
                    if (icon.getWidth() != ICON_SIZE || icon.getHeight() != ICON_SIZE) {
                        MrCrayfishDeviceMod.LOGGER.error("Incorrect icon size for " + identifier.toString() + " (Must be 14 by 14 pixels)");
                        continue;
                    }
                    int iconU = (index % 16) * ICON_SIZE;
                    int iconV = (index / 16) * ICON_SIZE;
                    g.drawImage(icon, iconU, iconV, ICON_SIZE, ICON_SIZE, null);
                    updateIcon(info, iconU, iconV);
                    index++;
                } else {
                    MrCrayfishDeviceMod.LOGGER.error("Icon for application '" + identifier.toString() + "' could not be found at '" + path + "'");
                }
            } catch (Exception e) {
                MrCrayfishDeviceMod.LOGGER.error("Unable to load icon for " + identifier.toString());
            }
        }

        g.dispose();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            ImageIO.write(atlas, "png", output);
            byte[] bytes = output.toByteArray();
            ByteArrayInputStream input = new ByteArrayInputStream(bytes);
            Minecraft.getInstance().getTextureManager().register(Laptop.ICON_TEXTURES, new DynamicTexture(NativeImage.read(input)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateIcon(AppInfo info, int iconU, int iconV) {
        ObfuscationReflectionHelper.setPrivateValue(AppInfo.class, info, iconU, "iconU");
        ObfuscationReflectionHelper.setPrivateValue(AppInfo.class, info, iconV, "iconV");
    }

    @Nullable
    public Application registerApplication(ResourceLocation identifier, Class<? extends Application> clazz) {
        if ("minecraft".equals(identifier.getNamespace())) {
            throw new IllegalArgumentException("Invalid identifier domain");
        }

        if (allowedApps == null) {
            allowedApps = new ArrayList<>();
        }
        if (SystemApplication.class.isAssignableFrom(clazz)) {
            allowedApps.add(new AppInfo(identifier, true));
        } else {
            allowedApps.add(new AppInfo(identifier, false));
        }

        try {
            Application application = clazz.getConstructor().newInstance();
            java.util.List<Application> apps = ObfuscationReflectionHelper.getPrivateValue(Laptop.class, null, "APPLICATIONS");
            assert apps != null;
            apps.add(application);

            Field field = Application.class.getDeclaredField("info");
            field.setAccessible(true);

//            ObfuscationReflectionHelper.setPrivateValue(Field.class, field, field.getModifiers() & ~Modifier.FINAL, "modifiers");
//            Field modifiers = Field.class.getDeclaredField("modifiers");
//            modifiers.setAccessible(true);
//            modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);

            field.set(application, generateAppInfo(identifier, clazz));

            return application;
        } catch (InstantiationException | IllegalAccessException | NoSuchFieldException | NoSuchMethodException |
                 InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    @NotNull
    private AppInfo generateAppInfo(ResourceLocation identifier, Class<? extends Application> clazz) {
        LOGGER.debug("Generating app info for " + identifier.toString());

        AppInfo info = new AppInfo(identifier, SystemApplication.class.isAssignableFrom(clazz));
        info.reload();
        return info;
    }

    public boolean registerPrint(ResourceLocation identifier, Class<? extends IPrint> classPrint) {
        LOGGER.debug("Registering print: " + identifier.toString());

        try {
            Constructor<? extends IPrint> constructor = classPrint.getConstructor();
            IPrint print = constructor.newInstance();
            Class<? extends IPrint.Renderer> classRenderer = print.getRenderer();
            try {
                IPrint.Renderer renderer = classRenderer.getConstructor().newInstance();
                Map<String, IPrint.Renderer> idToRenderer = ObfuscationReflectionHelper.getPrivateValue(PrintingManager.class, null, "registeredRenders");
                if (idToRenderer == null) {
                    idToRenderer = new HashMap<>();
                    ObfuscationReflectionHelper.setPrivateValue(PrintingManager.class, null, idToRenderer, "registeredRenders");
                }
                idToRenderer.put(identifier.toString(), renderer);
            } catch (InstantiationException e) {
                MrCrayfishDeviceMod.LOGGER.error("The print renderer '" + classRenderer.getName() + "' is missing an empty constructor and could not be registered!");
                return false;
            }
            return true;
        } catch (Exception e) {
            MrCrayfishDeviceMod.LOGGER.error("The print '" + classPrint.getName() + "' is missing an empty constructor and could not be registered!");
        }
        return false;
    }

    @SubscribeEvent
    public void onClientDisconnect(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        LOGGER.debug("Client disconnected from server");

        allowedApps = null;
        DeviceConfig.restore();
    }

    public void showNotification(CompoundTag tag) {
        LOGGER.debug("Showing notification");

        ClientNotification notification = ClientNotification.loadFromTag(tag);
        notification.push();
    }

    public boolean hasAllowedApplications() {
        return allowedApps != null;
    }

    public List<AppInfo> getAllowedApplications() {
        if (allowedApps == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(allowedApps);
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        LOGGER.info("Player logged in: " + event.getPlayer().getName());

        if (allowedApps != null) {
            PacketHandler.sendToClient(new SyncApplicationPacket(allowedApps), (ServerPlayer) event.getPlayer());
        }
        PacketHandler.sendToClient(new SyncConfigPacket(), (ServerPlayer) event.getPlayer());
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level world = event.getWorld();
        if (!event.getItemStack().isEmpty() && event.getItemStack().getItem() == Items.PAPER) {
            if (world.getBlockState(event.getPos()).getBlock() instanceof PrinterBlock) {
                event.setUseBlock(Event.Result.ALLOW);
            }
        }
    }

    @NotNull
    @Override
    public CompletableFuture<Void> reload(@NotNull PreparableReloadListener.PreparationBarrier preparationBarrier, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller preparationsProfiler, @NotNull ProfilerFiller reloadProfiler, @NotNull Executor backgroundExecutor, @NotNull Executor gameExecutor) {
        LOGGER.debug("Reloading resources from the Device Mod.");

        return CompletableFuture.runAsync(() -> {
            if (ApplicationManager.getAllApplications().size() > 0) {
                ApplicationManager.getAllApplications().forEach(AppInfo::reload);
                generateIconAtlas();
            }
        }, gameExecutor);
    }

    public static MrCrayfishDeviceMod getInstance() {
        return instance;
    }

    public static ResourceLocation res(String path) {
        return new ResourceLocation(Reference.MOD_ID, path);
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        // Some example code to dispatch IMC to another mod
        InterModComms.sendTo("device-mod", "helloworld", () -> {
            LOGGER.info("Hello world from the MDK");
            return "Hello world";
        });
    }

    private void processIMC(final InterModProcessEvent event) {
        // Some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m -> m.messageSupplier().get()).
                collect(Collectors.toList()));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // Register a new block here
            LOGGER.info("HELLO from Register Block");
        }
    }
}
