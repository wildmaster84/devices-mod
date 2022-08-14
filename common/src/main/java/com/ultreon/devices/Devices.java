package com.ultreon.devices;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.ultreon.devices.api.ApplicationManager;
import com.ultreon.devices.api.app.Application;
import com.ultreon.devices.api.print.IPrint;
import com.ultreon.devices.api.print.PrintingManager;
import com.ultreon.devices.api.task.TaskManager;
import com.ultreon.devices.api.utils.OnlineRequest;
import com.ultreon.devices.block.PrinterBlock;
import com.ultreon.devices.core.client.ClientNotification;
import com.ultreon.devices.core.client.debug.ClientAppDebug;
import com.ultreon.devices.core.io.task.*;
import com.ultreon.devices.core.network.task.TaskConnect;
import com.ultreon.devices.core.network.task.TaskGetDevices;
import com.ultreon.devices.core.network.task.TaskPing;
import com.ultreon.devices.core.print.task.TaskPrint;
import com.ultreon.devices.core.task.TaskInstallApp;
import com.ultreon.devices.init.DeviceItems;
import com.ultreon.devices.network.PacketHandler;
import com.ultreon.devices.network.task.SyncApplicationPacket;
import com.ultreon.devices.network.task.SyncConfigPacket;
import com.ultreon.devices.object.AppInfo;
import com.ultreon.devices.programs.*;
import com.ultreon.devices.programs.auction.MineBayApp;
import com.ultreon.devices.programs.auction.task.TaskAddAuction;
import com.ultreon.devices.programs.auction.task.TaskBuyItem;
import com.ultreon.devices.programs.auction.task.TaskGetAuctions;
import com.ultreon.devices.programs.debug.TextAreaApp;
import com.ultreon.devices.programs.email.EmailApp;
import com.ultreon.devices.programs.email.task.*;
import com.ultreon.devices.programs.example.ExampleApp;
import com.ultreon.devices.programs.example.task.TaskNotificationTest;
import com.ultreon.devices.programs.gitweb.GitWebApp;
import com.ultreon.devices.programs.snake.SnakeApp;
import com.ultreon.devices.programs.system.*;
import com.ultreon.devices.programs.system.task.*;
import com.ultreon.devices.util.ArchUtils;
import com.ultreon.devices.util.SiteRegistration;
import com.ultreon.ultranlang.*;
import com.ultreon.ultranlang.ast.Program;
import com.ultreon.ultranlang.error.LexerException;
import com.ultreon.ultranlang.error.ParserException;
import com.ultreon.ultranlang.error.SemanticException;
import com.ultreon.ultranlang.func.NativeCalls;
import com.ultreon.ultranlang.symbol.BuiltinTypeSymbol;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.injectables.targets.ArchitecturyTarget;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.Registries;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class Devices {
    public static final String MOD_ID = "devices";
    public static final CreativeModeTab TAB_DEVICE = CreativeTabRegistry.create(id("devices_tab_device"), () -> new ItemStack(DeviceItems.RED_LAPTOP.get()));
    public static final Supplier<Registries> REGISTRIES = Suppliers.memoize(() -> Registries.get(MOD_ID));
    private static final Pattern DEV_PREVIEW_PATTERN = Pattern.compile("\\d+\\.\\d+\\.\\d+-dev\\d+");
    private static final boolean IS_DEV_PREVIEW = DEV_PREVIEW_PATTERN.matcher(Reference.VERSION).matches();

    public static final List<SiteRegistration> SITE_REGISTRATIONS = new FreezableArrayList<>();

    public static final Logger LOGGER = LoggerFactory.getLogger("Devices Mod");

    public static final boolean DEVELOPER_MODE = false;
    private static MinecraftServer server;

    static List<AppInfo> allowedApps;

    public static void init() {
        if (ArchitecturyTarget.getCurrentTarget().equals("fabric")) {
            preInit();
            serverSetup();
        }
        EnvExecutor.runInEnv(Env.CLIENT, () -> () -> {
            ClientAppDebug.register();
            ClientModEvents.clientSetup(); //todo
        });

        //LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
        LOGGER.info("Doing some common setup.");

        PacketHandler.init();

        registerApplications();

        EnvExecutor.runInEnv(Env.CLIENT, () -> Devices::setupSiteRegistrations);

        setupEvents();

        EnvExecutor.runInEnv(Env.CLIENT, () -> Devices::setupClientEvents); //todo
        if (!ArchitecturyTarget.getCurrentTarget().equals("forge")) {
            loadComplete();
        }

        ultranLang: {
            var inputFile = new File("main.ulan");

            if (!inputFile.exists()) {
                LOGGER.error("File not found: {}", inputFile.getAbsolutePath());
            } else {
                SpiKt.setShouldLogInternalErrors(false);
                SpiKt.setShouldLogScope(false);
                SpiKt.setShouldLogStack(false);
                SpiKt.setShouldLogTokens(false);

                String text;
                try {
                    text = Files.readString(inputFile.toPath(), Charset.defaultCharset());
                } catch (IOException e) {
                    LOGGER.error("Failed to read file: {}", inputFile.getAbsolutePath(), e);
                    break ultranLang;
                }

                registerNativeFunctions();
                NativeCalls.INSTANCE.load();

                var lexer = new Lexer(text);
                Program tree;
                try {
                    var parser = new Parser(lexer);
                    tree = parser.parse();
                } catch (LexerException | ParserException e) {
                    if (SpiKt.getShouldLogInternalErrors()) e.printStackTrace();
                    LOGGER.error("Error parsing file: {}", e.getMessage());
                    break ultranLang;
                } catch (RuntimeException e) {
                    var cause = e.getCause();
                    while (cause instanceof InvocationTargetException || cause instanceof RuntimeException) {
                        cause = cause.getCause();
                    }
                    if (cause instanceof LexerException) {
                        if (SpiKt.getShouldLogInternalErrors()) cause.printStackTrace();
                        LOGGER.error("Error parsing file: {}", cause.getMessage());
                    } else if (cause instanceof ParserException) {
                        if (SpiKt.getShouldLogInternalErrors()) cause.printStackTrace();
                        LOGGER.error("Error parsing file: {}", cause.getMessage());
                    } else {
                        throw e;
                    }
                    break ultranLang;
                }

                var semanticAnalyzer = new SemanticAnalyzer();

                try {
                    semanticAnalyzer.visit(tree);
                } catch (SemanticException e) {
                    if (SpiKt.getShouldLogInternalErrors()) e.printStackTrace();
                    LOGGER.error("Error analyzing file: {}", e.getMessage());
                    break ultranLang;
                } catch (RuntimeException e) {
                    var cause = e.getCause();
                    while (cause instanceof InvocationTargetException || cause instanceof RuntimeException) {
                        cause = cause.getCause();
                    }
                    if (cause instanceof SemanticException) {
                        if (SpiKt.getShouldLogInternalErrors()) cause.printStackTrace();
                        LOGGER.error("Error analyzing file: {}", cause.getMessage());
                    } else {
                        throw e;
                    }
                    break ultranLang;
                }

                try {
                    var interpreter = new Interpreter(tree);
                    interpreter.interpret();
                } catch (Exception e) {
                    if (SpiKt.getShouldLogInternalErrors()) e.printStackTrace();
                    LOGGER.error("Error interpreting file: {}", e.getMessage());
                    break ultranLang;
                }
            }
        }
    }

    private static void registerNativeFunctions() {
        HashMap<String, String> params = new HashMap<>();
        params.put("level", BuiltinTypeSymbol.STRING);
        params.put("message", BuiltinTypeSymbol.STRING);
        NativeCalls.INSTANCE.register("log", params, ar -> {
            Object level = ar.get("level");
            if (level instanceof String levelName) {
                Object message = ar.get("message");
                if (message == null) message = "null";

                switch (levelName.toLowerCase(Locale.ROOT)) {
                    case "warn" -> LOGGER.warn(message.toString());
                    case "error" -> LOGGER.error(message.toString());
                    case "debug" -> LOGGER.debug(message.toString());
                    case "trace" -> LOGGER.trace(message.toString());
                    default -> LOGGER.info(message.toString());
                }
            } else {
                throw new IllegalArgumentException("Invalid level of type " + (level == null ? "null" : level.getClass().getName()));
            }
            return null;
        });
    }

    public static void preInit() {
        if (DEVELOPER_MODE && ArchUtils.isProduction()) {
            throw new LaunchException();
        }

        DeviceConfig.init();
    }


    public static boolean isDevelopmentPreview() {
        return IS_DEV_PREVIEW;
    }

    public static MinecraftServer getServer() {
        return server;
    }


    public static void serverSetup() {
        LOGGER.info("Doing some server setup.");
    }

    public static void loadComplete() {
        LOGGER.info("Doing some load complete handling.");
    }


    private static void registerApplications() {
        // Applications (Both)
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "diagnostics"), DiagnosticsApp.class);
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "settings"), SettingsApp.class);
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "bank"), BankApp.class);
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "file_browser"), FileBrowserApp.class);
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "gitweb"), GitWebApp.class);
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "note_stash"), NoteStashApp.class);
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "pixel_painter"), PixelPainterApp.class);
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "ender_mail"), EmailApp.class);
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "app_store"), AppStore.class);

        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "boat_racers"), BoatRacersApp.class);
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "mine_bay"), MineBayApp.class);

        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "snake"), SnakeApp.class);

        // Core
        TaskManager.registerTask(TaskUpdateApplicationData.class);
        TaskManager.registerTask(TaskPrint.class);
        TaskManager.registerTask(TaskUpdateSystemData.class);
        TaskManager.registerTask(TaskConnect.class);
        TaskManager.registerTask(TaskPing.class);
        TaskManager.registerTask(TaskGetDevices.class);

        // Bank
        TaskManager.registerTask(TaskDeposit.class);
        TaskManager.registerTask(TaskWithdraw.class);
        TaskManager.registerTask(TaskGetBalance.class);
        TaskManager.registerTask(TaskPay.class);
        TaskManager.registerTask(TaskAdd.class);
        TaskManager.registerTask(TaskRemove.class);

        // File browser
        TaskManager.registerTask(TaskSendAction.class);
        TaskManager.registerTask(TaskSetupFileBrowser.class);
        TaskManager.registerTask(TaskGetFiles.class);
        TaskManager.registerTask(TaskGetStructure.class);
        TaskManager.registerTask(TaskGetMainDrive.class);

        // App Store
        TaskManager.registerTask(TaskInstallApp.class);

        // Ender Mail
        TaskManager.registerTask(TaskUpdateInbox.class);
        TaskManager.registerTask(TaskSendEmail.class);
        TaskManager.registerTask(TaskCheckEmailAccount.class);
        TaskManager.registerTask(TaskRegisterEmailAccount.class);
        TaskManager.registerTask(TaskDeleteEmail.class);
        TaskManager.registerTask(TaskViewEmail.class);

        // Auction
        TaskManager.registerTask(TaskAddAuction.class);
        TaskManager.registerTask(TaskGetAuctions.class);
        TaskManager.registerTask(TaskBuyItem.class);

        if (DEVELOPER_MODE) {
            // Applications (Developers)
            ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "example"), ExampleApp.class);
            ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "icons"), IconsApp.class);
            ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "text_area"), TextAreaApp.class);
            ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "test"), TestApp.class);

            TaskManager.registerTask(TaskNotificationTest.class);
        }

        EnvExecutor.runInEnv(Env.CLIENT, () -> () -> {
            PrintingManager.registerPrint(new ResourceLocation(Reference.MOD_ID, "picture"), PixelPainterApp.PicturePrint.class);
        });
    }

    @ExpectPlatform
    private static List<Application> getAPPLICATIONS() {
        throw new AssertionError();
    }

    public static void setAllowedApps(List<AppInfo> allowedApps) {
        Devices.allowedApps = allowedApps;
    }

    @Nullable
    public static Application registerApplication(ResourceLocation identifier, Class<? extends Application> clazz) {
        if ("minecraft".equals(identifier.getNamespace())) {
            throw new IllegalArgumentException("Identifier cannot be \"minecraft\"!");
        }

        if (allowedApps == null) {
            allowedApps = new ArrayList<>();
        }
        if (SystemApp.class.isAssignableFrom(clazz)) {
            allowedApps.add(new AppInfo(identifier, true));
        } else {
            allowedApps.add(new AppInfo(identifier, false));
        }

        AtomicReference<Application> application = new AtomicReference<>(null);
        EnvExecutor.runInEnv(Env.CLIENT, () -> () -> {
            try {
                Application app = clazz.getConstructor().newInstance();
                List<Application> apps = getAPPLICATIONS(); /*ObfuscationReflectionHelper.getPrivateValue(Laptop.class, null, "APPLICATIONS");*/
                assert apps != null;
                apps.add(app);

                app.setInfo( generateAppInfo(identifier, clazz));

                application.set(app);
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                     InvocationTargetException e) {
                e.printStackTrace();
            }
        });
        return application.get();
    }

    @NotNull
    private static AppInfo generateAppInfo(ResourceLocation identifier, Class<? extends Application> clazz) {
        LOGGER.debug("Generating app info for " + identifier.toString());

        AppInfo info = new AppInfo(identifier, SystemApp.class.isAssignableFrom(clazz));
        info.reload();
        return info;
    }

    @ExpectPlatform
    private static Map<String, IPrint.Renderer> getRegisteredRenders(){
        throw new AssertionError();
    }

    @ExpectPlatform
    private static void setRegisteredRenders(Map<String, IPrint.Renderer> map){
        throw new AssertionError();
    }

    public static boolean registerPrint(ResourceLocation identifier, Class<? extends IPrint> classPrint) {
        LOGGER.debug("Registering print: " + identifier.toString());

        try {
            Constructor<? extends IPrint> constructor = classPrint.getConstructor();
            IPrint print = constructor.newInstance();
            Class<? extends IPrint.Renderer> classRenderer = print.getRenderer();
            try {
                IPrint.Renderer renderer = classRenderer.getConstructor().newInstance();
                Map<String, IPrint.Renderer> idToRenderer = getRegisteredRenders(); //ObfuscationReflectionHelper.getPrivateValue(PrintingManager.class, null, "registeredRenders");
                if (idToRenderer == null) {
                    idToRenderer = new HashMap<>();
                    setRegisteredRenders(idToRenderer);
                    //ObfuscationReflectionHelper.setPrivateValue(PrintingManager.class, null, idToRenderer, "registeredRenders");
                }
                idToRenderer.put(identifier.toString(), renderer);
            } catch (InstantiationException e) {
                Devices.LOGGER.error("The print renderer '" + classRenderer.getName() + "' is missing an empty constructor and could not be registered!");
                return false;
            }
            return true;
        } catch (Exception e) {
            Devices.LOGGER.error("The print '" + classPrint.getName() + "' is missing an empty constructor and could not be registered!");
        }
        return false;
    }

    public static void showNotification(CompoundTag tag) {
        LOGGER.debug("Showing notification");

        EnvExecutor.runInEnv(Env.CLIENT, () -> () -> {
            ClientNotification notification = ClientNotification.loadFromTag(tag);
            notification.push();
        });
    }

    public static boolean hasAllowedApplications() {
        return allowedApps != null;
    }

    public static List<AppInfo> getAllowedApplications() {
        if (allowedApps == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(allowedApps);
    }

    public static ResourceLocation res(String path) {
        return new ResourceLocation(Devices.MOD_ID, path);
    }

//    private void enqueueIMC(final InterModEnqueueEvent event) {
//        // Check for self availability.
//        InterModComms.sendTo(Reference.MOD_ID, "availability", () -> {
//            LOGGER.info("IMC is working correctly");
//            return "Hello world";
//        });
//    }
//
//    private void processIMC(final InterModProcessEvent event) {
//        event.getIMCStream().forEachOrdered(imcMessage -> {
//            // Availability IMC handling.
//            if (imcMessage.method().equals("availability")) {
//                LOGGER.info("Received message from " + imcMessage.senderModId() + ": " + imcMessage.messageSupplier().get());
//            }
//        });
//    }

    private static void setupClientEvents() {
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register((player -> {
            LOGGER.debug("Client disconnected from server");

            allowedApps = null;
            DeviceConfig.restore();
        }));
    }

    private static void setupEvents() {
        LifecycleEvent.SERVER_STARTING.register((instance -> server = instance));
        LifecycleEvent.SERVER_STOPPED.register(instance -> server = null);
        InteractionEvent.RIGHT_CLICK_BLOCK.register(((player, hand, pos, face) -> {
            Level level = player.getLevel();
            if (!player.getItemInHand(hand).isEmpty() && player.getItemInHand(hand).getItem() == Items.PAPER) {
                if (level.getBlockState(pos).getBlock() instanceof PrinterBlock) {
                    return EventResult.interruptTrue();
                    //event.setUseBlock(Event.Result.ALLOW); //todo
                }
            }
            return EventResult.pass();
        }));

        PlayerEvent.PLAYER_JOIN.register((player -> {
            LOGGER.info("Player logged in: " + player.getName());

            if (allowedApps != null) {
                PacketHandler.sendToClient(new SyncApplicationPacket(allowedApps), player);
            }
            PacketHandler.sendToClient(new SyncConfigPacket(), player);
        }));
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
                            @SuppressWarnings("UnnecessaryLocalVariable") var string = s;
                            @SuppressWarnings("UnnecessaryLocalVariable") var _registrant = registrant;
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

    public static ResourceLocation id(String id) {
        return new ResourceLocation(MOD_ID, id);
    }
    

}