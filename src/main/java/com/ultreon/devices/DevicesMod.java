package com.ultreon.devices;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import com.ultreon.devices.api.ApplicationManager;
import com.ultreon.devices.api.app.Application;
import com.ultreon.devices.api.print.IPrint;
import com.ultreon.devices.api.print.PrintingManager;
import com.ultreon.devices.api.task.TaskManager;
import com.ultreon.devices.api.utils.OnlineRequest;
import com.ultreon.devices.block.PrinterBlock;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.core.client.ClientNotification;
import com.ultreon.devices.core.io.task.*;
import com.ultreon.devices.core.network.task.TaskConnect;
import com.ultreon.devices.core.network.task.TaskGetDevices;
import com.ultreon.devices.core.network.task.TaskPing;
import com.ultreon.devices.core.print.task.TaskPrint;
import com.ultreon.devices.core.task.TaskInstallApp;
import com.ultreon.devices.event.WorldDataHandler;
import com.ultreon.devices.init.RegistrationHandler;
import com.ultreon.devices.network.PacketHandler;
import com.ultreon.devices.network.task.SyncApplicationPacket;
import com.ultreon.devices.network.task.SyncConfigPacket;
import com.ultreon.devices.object.AppInfo;
import com.ultreon.devices.programs.ApplicationBoatRacers;
import com.ultreon.devices.programs.ApplicationIcons;
import com.ultreon.devices.programs.email.ApplicationEmail;
import com.ultreon.devices.programs.email.task.*;
import com.ultreon.devices.programs.gitweb.ApplicationGitWeb;
import com.ultreon.devices.programs.system.*;
import com.ultreon.devices.programs.system.task.*;
import com.ultreon.devices.util.SiteRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.loading.DatagenModLoader;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
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
import java.util.function.Predicate;
import java.util.regex.Pattern;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Reference.MOD_ID)
public class DevicesMod implements PreparableReloadListener {
    private static final Pattern DEV_PREVIEW_PATTERN = Pattern.compile("\\d+\\.\\d+\\.\\d+-dev\\d+");
    private static final boolean IS_DEV_PREVIEW = DEV_PREVIEW_PATTERN.matcher(Reference.VERSION).matches();

    private static DevicesMod instance;

    public static final CreativeModeTab TAB_DEVICE = new DeviceTab("devices_tab_device");

    public static final List<SiteRegistration> SITE_REGISTRATIONS = new FreezableArrayList<>();

    public static final Logger LOGGER = LogUtils.getLogger();

    public static final boolean DEVELOPER_MODE = false;
    private static MinecraftServer server;

    List<AppInfo> allowedApps;

    public DevicesMod() throws LaunchException {
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
        forgeEventBus.register(new WorldDataHandler());

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

    public static boolean isDevelopmentPreview() {
        return IS_DEV_PREVIEW;
    }

    public static MinecraftServer getServer() {
        return server;
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());

        LOGGER.info("Doing some common setup.");

        PacketHandler.init();

        registerApplications();

        setupSiteRegistrations();

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
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "ender_mail"), ApplicationEmail.class);
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "app_store"), ApplicationAppStore.class);

        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "boat_racers"), ApplicationBoatRacers.class);

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

        // App Store
        TaskManager.registerTask(TaskInstallApp.class);

        //Ender Mail
        TaskManager.registerTask(TaskUpdateInbox.class);
        TaskManager.registerTask(TaskSendEmail.class);
        TaskManager.registerTask(TaskCheckEmailAccount.class);
        TaskManager.registerTask(TaskRegisterEmailAccount.class);
        TaskManager.registerTask(TaskDeleteEmail.class);
        TaskManager.registerTask(TaskViewEmail.class);

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
            BufferedImage icon = ImageIO.read(Objects.requireNonNull(DevicesMod.class.getResourceAsStream("/assets/" + Reference.MOD_ID + "/textures/app/icon/missing.png")));
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
                InputStream input = DevicesMod.class.getResourceAsStream(path);
                if (input != null) {
                    BufferedImage icon = ImageIO.read(input);
                    if (icon.getWidth() != ICON_SIZE || icon.getHeight() != ICON_SIZE) {
                        DevicesMod.LOGGER.error("Incorrect icon size for " + identifier.toString() + " (Must be 14 by 14 pixels)");
                        continue;
                    }
                    int iconU = (index % 16) * ICON_SIZE;
                    int iconV = (index / 16) * ICON_SIZE;
                    g.drawImage(icon, iconU, iconV, ICON_SIZE, ICON_SIZE, null);
                    updateIcon(info, iconU, iconV);
                    index++;
                } else {
                    DevicesMod.LOGGER.error("Icon for application '" + identifier.toString() + "' could not be found at '" + path + "'");
                }
            } catch (Exception e) {
                DevicesMod.LOGGER.error("Unable to load icon for " + identifier.toString());
            }
        }

        g.dispose();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            ImageIO.write(atlas, "png", output);
            byte[] bytes = output.toByteArray();
            ByteArrayInputStream input = new ByteArrayInputStream(bytes);
            Minecraft.getInstance().submit(() -> {
                try {
                    Minecraft.getInstance().getTextureManager().register(Laptop.ICON_TEXTURES, new DynamicTexture(NativeImage.read(input)));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
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
            List<Application> apps = ObfuscationReflectionHelper.getPrivateValue(Laptop.class, null, "APPLICATIONS");
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
                DevicesMod.LOGGER.error("The print renderer '" + classRenderer.getName() + "' is missing an empty constructor and could not be registered!");
                return false;
            }
            return true;
        } catch (Exception e) {
            DevicesMod.LOGGER.error("The print '" + classPrint.getName() + "' is missing an empty constructor and could not be registered!");
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
        Level level = event.getWorld();
        if (!event.getItemStack().isEmpty() && event.getItemStack().getItem() == Items.PAPER) {
            if (level.getBlockState(event.getPos()).getBlock() instanceof PrinterBlock) {
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

    public static DevicesMod getInstance() {
        return instance;
    }

    public static ResourceLocation res(String path) {
        return new ResourceLocation(Reference.MOD_ID, path);
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        // Check for self availability.
        InterModComms.sendTo(Reference.MOD_ID, "availability", () -> {
            LOGGER.info("IMC is working correctly");
            return "Hello world";
        });
    }

    private void processIMC(final InterModProcessEvent event) {
        event.getIMCStream().forEachOrdered(imcMessage -> {
            // Availability IMC handling.
            if (imcMessage.method().equals("availability")) {
                LOGGER.info("Received message from " + imcMessage.senderModId() + ": " + imcMessage.messageSupplier().get());
            }
        });
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("A world or server is starting, setting server reference");

        server = event.getServer();
    }

    @SubscribeEvent
    public void onServerStopped(ServerStoppedEvent event) {
        LOGGER.info("Server has stopped, nullifying server reference.");

        server = null;
    }

    private static void setupSiteRegistrations() {
        OnlineRequest.getInstance().make("https://raw.githubusercontent.com/Jab125/gitweb-sites/main/site_registrations.json", (success, response) -> {
            if (success) {
                //Minecraft.getInstance().doRunTask(() -> {
                JsonArray array = JsonParser.parseString(response).getAsJsonArray();
                for (JsonElement element : array) {
                    var registrant = element.getAsJsonObject().get("registrant") != null ? element.getAsJsonObject().get("registrant").getAsString() : null;
                    @SuppressWarnings("all") //no
                    var dev = element.getAsJsonObject().get("dev") != null ? element.getAsJsonObject().get("dev").getAsBoolean() : false;
                    var site = element.getAsJsonObject().get("site").getAsString();
                    if (dev && !IS_DEV_PREVIEW) {
                        continue;
                    }
                    for (JsonElement jsonElement : element.getAsJsonObject().get("registrations").getAsJsonArray()) {
                        var a = jsonElement.getAsJsonObject().keySet();
                        var d = jsonElement.getAsJsonObject();
                        for (String s : a) {
                            var type = d.get(s).getAsString();
                            var string = s;
                            var _registrant = registrant;
                            SITE_REGISTRATIONS.add(new SiteRegistration(registrant, string, type, site));
                        }
                    }
                }
//                System.out.println(SITE_REGISTRATIONS);
//                System.exit(0);
            } else {
                // TODO error handling
            }
            ((FreezableArrayList<SiteRegistration>)SITE_REGISTRATIONS).freeze();
        });
    }

    private static class FreezableArrayList<T> extends ArrayList<T> {
        private boolean frozen = false;
        private void freeze() {
            frozen = true;
        }

        private void freezeCheck() {
            if (frozen) throw new IllegalStateException("Already frozen!");
        }

        @Override
        public boolean add(T t) {
            freezeCheck();
            return super.add(t);
        }

        @Override
        public boolean addAll(Collection<? extends T> c) {
            freezeCheck();
            return super.addAll(c);
        }

        @Override
        public void add(int index, T element) {
            freezeCheck();
            super.add(index, element);
        }

        @Override
        protected void removeRange(int fromIndex, int toIndex) {
            freezeCheck();
            super.removeRange(fromIndex, toIndex);
        }

        @Override
        public boolean remove(Object o) {
            freezeCheck();
            return super.remove(o);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            freezeCheck();
            return super.removeAll(c);
        }

        @Override
        public boolean removeIf(Predicate<? super T> filter) {
            freezeCheck();
            return super.removeIf(filter);
        }

        @Override
        public T remove(int index) {
            freezeCheck();
            return super.remove(index);
        }
    }
}
