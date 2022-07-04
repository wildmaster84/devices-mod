package com.mrcrayfish.device;

import com.mojang.blaze3d.platform.NativeImage;
import com.mrcrayfish.device.api.ApplicationManager;
import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.print.IPrint;
import com.mrcrayfish.device.api.print.PrintingManager;
import com.mrcrayfish.device.block.PrinterBlock;
import com.mrcrayfish.device.block.entity.renderer.LaptopRenderer;
import com.mrcrayfish.device.block.entity.renderer.PaperRenderer;
import com.mrcrayfish.device.block.entity.renderer.PrinterRenderer;
import com.mrcrayfish.device.block.entity.renderer.RouterRenderer;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.core.client.ClientNotification;
import com.mrcrayfish.device.init.DeviceBlockEntities;
import com.mrcrayfish.device.init.RegistrationHandler;
import com.mrcrayfish.device.network.PacketHandler;
import com.mrcrayfish.device.network.task.SyncApplicationPacket;
import com.mrcrayfish.device.network.task.SyncConfigPacket;
import com.mrcrayfish.device.object.AppInfo;
import com.mrcrayfish.device.programs.system.SystemApplication;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.loading.DatagenModLoader;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@SuppressWarnings("unused")
public abstract class CommonProxy {
    List<AppInfo> allowedApps;
    int hashCode = -1;

    public MrCrayfishDeviceMod getDeviceMod() {
        return MrCrayfishDeviceMod.getInstance();
    }

    public void init() {
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener((FMLCommonSetupEvent event) -> this.commonSetup());

        RegistrationHandler.init(modEventBus);
    }

    /**
     * This is called from a handler of an FML-Setup event.
     * The FML-Setup event is {@link FMLCommonSetupEvent} for the {@link CommonProxy}, and {@link FMLClientSetupEvent} for the {@link Client ClientProxy}.
     */
    public abstract void setup();

    public final void commonSetup() {
        PacketHandler.init();
    }

    public void loadComplete() {

    }

    @Nullable
    public Application registerApplication(ResourceLocation identifier, Class<? extends Application> clazz) {
        if (allowedApps == null) {
            allowedApps = new ArrayList<>();
        }
        if (SystemApplication.class.isAssignableFrom(clazz)) {
            allowedApps.add(new AppInfo(identifier, true));
        } else {
            allowedApps.add(new AppInfo(identifier, false));
        }
        return null;
    }

    public boolean registerPrint(ResourceLocation identifier, Class<? extends IPrint> classPrint) {
        return true;
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

    public void showNotification(CompoundTag tag) {
    }

    public static class Client extends CommonProxy implements PreparableReloadListener {
        @Override
        public void init() {
            super.init();

            if (!DatagenModLoader.isRunningDataGen()) {
                ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener(this);
            }

            IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
            modEventBus.addListener((FMLClientSetupEvent event) -> this.setup());
        }

        @Override
        public void setup() {
//            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLaptop.class, new LaptopRenderer());
//            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPrinter.class, new PrinterRenderer());
//            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPaper.class, new PaperRenderer());
//            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRouter.class, new RouterRenderer());
//            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityOfficeChair.class, new OfficeChairRenderer());

            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerRenderers);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerLayerDefinitions);

            if (MrCrayfishDeviceMod.DEVELOPER_MODE) {
                Laptop.addWallpaper(new ResourceLocation("cdm:textures/gui/developer_wallpaper.png"));
            } else {
                Laptop.addWallpaper(new ResourceLocation("cdm:textures/gui/laptop_wallpaper_1.png"));
                Laptop.addWallpaper(new ResourceLocation("cdm:textures/gui/laptop_wallpaper_2.png"));
                Laptop.addWallpaper(new ResourceLocation("cdm:textures/gui/laptop_wallpaper_3.png"));
                Laptop.addWallpaper(new ResourceLocation("cdm:textures/gui/laptop_wallpaper_4.png"));
                Laptop.addWallpaper(new ResourceLocation("cdm:textures/gui/laptop_wallpaper_5.png"));
                Laptop.addWallpaper(new ResourceLocation("cdm:textures/gui/laptop_wallpaper_6.png"));
                Laptop.addWallpaper(new ResourceLocation("cdm:textures/gui/laptop_wallpaper_7.png"));
            }
        }

        private void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(DeviceBlockEntities.LAPTOP.get(), LaptopRenderer::new);
            event.registerBlockEntityRenderer(DeviceBlockEntities.PRINTER.get(), PrinterRenderer::new);
            event.registerBlockEntityRenderer(DeviceBlockEntities.PAPER.get(), PaperRenderer::new);
            event.registerBlockEntityRenderer(DeviceBlockEntities.ROUTER.get(), RouterRenderer::new);
        }

        private void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(PrinterRenderer.PaperModel.LAYER_LOCATION, PrinterRenderer.PaperModel::createBodyLayer);
        }

        @Override
        public void loadComplete() {
            generateIconAtlas();
        }

        private void generateIconAtlas() {
            final int ICON_SIZE = 14;
            int index = 0;

            BufferedImage atlas = new BufferedImage(ICON_SIZE * 16, ICON_SIZE * 16, BufferedImage.TYPE_INT_ARGB);
            Graphics g = atlas.createGraphics();

            try {
                BufferedImage icon = ImageIO.read(Objects.requireNonNull(Client.class.getResourceAsStream("/assets/" + Reference.MOD_ID + "/textures/app/icon/missing.png")));
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
                    InputStream input = Client.class.getResourceAsStream(path);
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
        @Override
        public Application registerApplication(ResourceLocation identifier, Class<? extends Application> clazz) {
            if ("minecraft".equals(identifier.getNamespace())) {
                throw new IllegalArgumentException("Invalid identifier domain");
            }

            try {
                Application application = clazz.getConstructor().newInstance();
                java.util.List<Application> apps = ObfuscationReflectionHelper.getPrivateValue(Laptop.class, null, "APPLICATIONS");
                assert apps != null;
                apps.add(application);

                Field field = Application.class.getDeclaredField("info");
                field.setAccessible(true);

                Field modifiers = Field.class.getDeclaredField("modifiers");
                modifiers.setAccessible(true);
                modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);

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
            AppInfo info = new AppInfo(identifier, SystemApplication.class.isAssignableFrom(clazz));
            info.reload();
            return info;
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        public boolean registerPrint(ResourceLocation identifier, Class<? extends IPrint> classPrint) {
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
            allowedApps = null;
            DeviceConfig.restore();
        }

        @Override
        public void showNotification(CompoundTag tag) {
            ClientNotification notification = ClientNotification.loadFromTag(tag);
            notification.push();
        }

        @NotNull
        @Override
        public CompletableFuture<Void> reload(@NotNull PreparationBarrier preparationBarrier, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller preparationsProfiler, @NotNull ProfilerFiller reloadProfiler, @NotNull Executor backgroundExecutor, @NotNull Executor gameExecutor) {
            return CompletableFuture.runAsync(() -> {
                if (ApplicationManager.getAllApplications().size() > 0) {
                    ApplicationManager.getAllApplications().forEach(AppInfo::reload);
                    generateIconAtlas();
                }
            }, gameExecutor);
        }
    }

    public static class Server extends CommonProxy {
        @Override
        public void init() {
            super.init();

            IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
            modEventBus.addListener((FMLDedicatedServerSetupEvent event) -> setup());
        }

        @Override
        public void setup() {

        }
    }
}
