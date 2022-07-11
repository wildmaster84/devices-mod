package com.ultreon.devices.core;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.kinds.App;
import com.ultreon.devices.DevicesMod;
import com.ultreon.devices.Reference;
import com.ultreon.devices.api.ApplicationManager;
import com.ultreon.devices.api.app.*;
import com.ultreon.devices.api.app.Dialog;
import com.ultreon.devices.api.app.System;
import com.ultreon.devices.api.app.component.Image;
import com.ultreon.devices.api.io.Drive;
import com.ultreon.devices.api.io.File;
import com.ultreon.devices.api.task.Callback;
import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.api.task.TaskManager;
import com.ultreon.devices.api.utils.RenderUtil;
import com.ultreon.devices.block.entity.LaptopBlockEntity;
import com.ultreon.devices.core.task.TaskInstallApp;
import com.ultreon.devices.object.AppInfo;
import com.ultreon.devices.programs.system.SystemApplication;
import com.ultreon.devices.programs.system.component.FileBrowser;
import com.ultreon.devices.programs.system.task.TaskUpdateApplicationData;
import com.ultreon.devices.programs.system.task.TaskUpdateSystemData;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

//TODO Intro message (created by mrcrayfish, donate here)

/**
 * Laptop GUI class.
 *
 * @author MrCrayfish, Qboi123
 */
public class Laptop extends Screen implements System {
    public static final int ID = 1;
    public static final ResourceLocation ICON_TEXTURES = new ResourceLocation(Reference.MOD_ID, "textures/atlas/app_icons.png");
    public static final int ICON_SIZE = 14;
    //    public static final Font font = new LaptopFont(Minecraft.getInstance());
    public static final Font font = Minecraft.getInstance().font;
    private static final ResourceLocation LAPTOP_GUI = new ResourceLocation(Reference.MOD_ID, "textures/gui/laptop.png");
    private static final List<Application> APPLICATIONS = new ArrayList<>();
    private static final List<ResourceLocation> WALLPAPERS = new ArrayList<>();

    private static final int BORDER = 10;
    private static final int DEVICE_WIDTH = 384;
    static final int SCREEN_WIDTH = DEVICE_WIDTH - BORDER * 2;
    private static final int DEVICE_HEIGHT = 216;
    static final int SCREEN_HEIGHT = DEVICE_HEIGHT - BORDER * 2;
    private static final List<Runnable> tasks = new CopyOnWriteArrayList<>();
    private static System system;
    private static BlockPos pos;
    private static Drive mainDrive;
    private final Settings settings;
    private final TaskBar bar;
    private final Window<?>[] windows; // Todo: make this a list
    private final CompoundTag appData;
    private final CompoundTag systemData;
    protected List<AppInfo> installedApps = new ArrayList<>();
    private Layout context = null;
    private int currentWallpaper;
    private int lastMouseX, lastMouseY;
    private boolean dragging = false;
    private final IntArraySet pressed = new IntArraySet();

    /**
     * Creates a new laptop GUI.
     *
     * @param laptop the block entity of the laptop in-game.
     */
    public Laptop(LaptopBlockEntity laptop) {
        super(new TextComponent("Laptop"));

        this.appData = laptop.getApplicationData();
        this.systemData = laptop.getSystemData();
        this.windows = new Window[5];
        this.settings = Settings.fromTag(systemData.getCompound("Settings"));
        this.bar = new TaskBar(this);
        this.currentWallpaper = systemData.getInt("CurrentWallpaper");
        if (currentWallpaper < 0 || currentWallpaper >= WALLPAPERS.size()) {
            this.currentWallpaper = 0;
        }
        Laptop.system = this;
        Laptop.pos = laptop.getBlockPos();
    }

    /**
     * Returns the position of the laptop the player is currently using. This method can ONLY be
     * called when the laptop GUI is open, otherwise it will return a null position.
     *
     * @return the position of the laptop currently in use
     */
    @Nullable
    public static BlockPos getPos() {
        return pos;
    }

    /**
     * Add a wallpaper to the list of available wallpapers.
     *
     * @param wallpaper location to the wallpaper texture, if null the wallpaper will not be added.
     */
    public static void addWallpaper(ResourceLocation wallpaper) {
        if (wallpaper != null) {
            WALLPAPERS.add(wallpaper);
        }
    }

    public static System getSystem() {
        return system;
    }

    @Nullable
    public static Drive getMainDrive() {
        return mainDrive;
    }

    public static void setMainDrive(Drive mainDrive) {
        if (Laptop.mainDrive == null) {
            Laptop.mainDrive = mainDrive;
        }
    }

    /**
     * Run a task later in render thread.
     *
     * @param task the task to run.
     */
    public static void runLater(Runnable task) {
        tasks.add(task);
    }

    /**
     * Initialize the Laptop GUI.
     */
    @Override
    public void init() {
        Minecraft.getInstance().keyboardHandler.setSendRepeatsToGui(true);
        int posX = (width - DEVICE_WIDTH) / 2;
        int posY = (height - DEVICE_HEIGHT) / 2;
        bar.init(posX + BORDER, posY + DEVICE_HEIGHT - 28);

        installedApps.clear();
        ListTag list = systemData.getList("InstalledApps", Tag.TAG_STRING);
        for (int i = 0; i < list.size(); i++) {
            AppInfo info = ApplicationManager.getApplication(list.getString(i));
            if (info != null) {
                installedApps.add(info);
            }
        }
        installedApps.sort(AppInfo.SORT_NAME);
    }

    @Override
    public void removed() {
        Minecraft.getInstance().keyboardHandler.setSendRepeatsToGui(false);

        /* Close all windows and sendTask application data */
        for (Window<?> window : windows) {
            if (window != null) {
                window.close();
            }
        }

        /* Send system data */
        this.updateSystemData();

        Laptop.pos = null;
        Laptop.system = null;
        Laptop.mainDrive = null;
    }

    private void updateSystemData() {
        systemData.putInt("CurrentWallpaper", currentWallpaper);
        systemData.put("Settings", settings.toTag());

        ListTag tagListApps = new ListTag();
        installedApps.forEach(info -> tagListApps.add(StringTag.valueOf(info.getFormattedId())));
        systemData.put("InstalledApps", tagListApps);

        TaskManager.sendTask(new TaskUpdateSystemData(pos, systemData));
    }

    /**
     * Handles Minecraft GUI resizing.
     *
     * @param minecraft the Minecraft instance
     * @param width     the new width
     * @param height    the new height
     */
    @Override
    public void resize(@NotNull Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
        for (var window : windows) {
            if (window != null) {
                window.content.markForLayoutUpdate();
            }
        }
    }

    /**
     * Ticking the laptop.
     */
    @Override
    public void tick() {
        bar.onTick();

        for (var window : windows) {
            if (window != null) {
                window.onTick();
            }
        }

        FileBrowser.refreshList = false;
    }

    /**
     * Render the laptop screen.
     *
     * @param pose         the pose stack.
     * @param mouseX       the current mouse X position.
     * @param mouseY       the current mouse Y position.
     * @param partialTicks the rendering partial ticks that forge give use (which is useless here).
     */
    @Override
    public void render(final @NotNull PoseStack pose, final int mouseX, final int mouseY, float partialTicks) {
        for (Runnable task : tasks) {
            task.run();
        }
        tasks.clear();

        // Fixes the strange partialTicks that Forge decided to give us
        final float frameTime = Minecraft.getInstance().getFrameTime();

        this.renderBackground(pose);

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, LAPTOP_GUI);

        //*************************//
        //     Physical Screen     //
        //*************************//
        int posX = (width - DEVICE_WIDTH) / 2;
        int posY = (height - DEVICE_HEIGHT) / 2;

        // Corners
        blit(pose, posX, posY, 0, 0, BORDER, BORDER); // TOP-LEFT
        blit(pose, posX + DEVICE_WIDTH - BORDER, posY, 11, 0, BORDER, BORDER); // TOP-RIGHT
        blit(pose, posX + DEVICE_WIDTH - BORDER, posY + DEVICE_HEIGHT - BORDER, 11, 11, BORDER, BORDER); // BOTTOM-RIGHT
        blit(pose, posX, posY + DEVICE_HEIGHT - BORDER, 0, 11, BORDER, BORDER); // BOTTOM-LEFT

        // Edges
        Gui.blit(pose, posX + BORDER, posY, SCREEN_WIDTH, BORDER, 10, 0, 1, BORDER, 256, 256); // TOP
        Gui.blit(pose, posX + DEVICE_WIDTH - BORDER, posY + BORDER, BORDER, SCREEN_HEIGHT, 11, 10, BORDER, 1, 256, 256); // RIGHT
        Gui.blit(pose, posX + BORDER, posY + DEVICE_HEIGHT - BORDER, SCREEN_WIDTH, BORDER, 10, 11, 1, BORDER, 256, 256); // BOTTOM
        Gui.blit(pose, posX, posY + BORDER, BORDER, SCREEN_HEIGHT, 0, 11, BORDER, 1, 256, 256); // LEFT

        // Center
        Gui.blit(pose, posX + BORDER, posY + BORDER, SCREEN_WIDTH, SCREEN_HEIGHT, 10, 10, 1, 1, 256, 256);

        //*******************//
        //     Wallpaper     //
        //*******************//
        RenderSystem.setShaderTexture(0, WALLPAPERS.get(currentWallpaper));
        RenderUtil.drawRectWithTexture(pose, posX + 10, posY + 10, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, 512, 288);

        //************************************//
        // Draw the watermark on the desktop. //
        //************************************//
        if (!DevicesMod.DEVELOPER_MODE) {
            if (DevicesMod.isDevelopmentPreview()) {
                drawString(pose, font, "Development Preview - " + Reference.VERSION, posX + BORDER + 5, posY + BORDER + 5, Color.WHITE.getRGB());
            } else {
                drawString(pose, font, "Alpha " + Reference.VERSION, posX + BORDER + 5, posY + BORDER + 5, Color.WHITE.getRGB());
            }
        } else {
            drawString(pose, font, "Developer Version - " + Reference.VERSION, posX + BORDER + 5, posY + BORDER + 5, Color.WHITE.getRGB());
        }

        boolean insideContext = false;
        if (context != null) {
            insideContext = isMouseInside(mouseX, mouseY, context.xPosition, context.yPosition, context.xPosition + context.width, context.yPosition + context.height);
        }

        Image.CACHE.forEach((s, cachedImage) -> cachedImage.delete());

        //****************//
        //     Window     //
        //****************//
        pose.pushPose();
        {
            Window<?>[] windows1 = Arrays.stream(windows).filter(Objects::nonNull).toArray(Window<?>[]::new);
            for (int i = windows1.length - 1; i >= 0; i--) {
                var window = windows1[i];
                if (window != null) {
                    if (i == 0) {
                        window.render(pose, this, minecraft, posX + BORDER, posY + BORDER, mouseX, mouseY, !insideContext, partialTicks);
                    } else {
                        window.render(pose, this, minecraft, posX + BORDER, posY + BORDER, Integer.MAX_VALUE, Integer.MAX_VALUE, false, partialTicks);
                    }
                    pose.translate(0, 0, 100);
                }
            }
        }
        pose.popPose();

        //****************************//
        // Render the Application Bar //
        //****************************//
        bar.render(pose, this, minecraft, posX + 10, posY + DEVICE_HEIGHT - 28, mouseX, mouseY, frameTime);

        if (context != null) {
            context.render(pose, this, minecraft, context.xPosition, context.yPosition, mouseX, mouseY, true, frameTime);
        }

        Image.CACHE.entrySet().removeIf(entry -> {
            Image.CachedImage cachedImage = entry.getValue();
            if (cachedImage.isDynamic() && cachedImage.isPendingDeletion()) {
                int texture = cachedImage.getTextureId();
                if (texture != -1) {
                    RenderSystem.deleteTexture(texture);
                }
                return true;
            }
            return false;
        });

        super.render(pose, mouseX, mouseY, frameTime);
    }

    private boolean isMouseInside(int mouseX, int mouseY, int startX, int startY, int endX, int endY) {
        return mouseX >= startX && mouseX <= endX && mouseY >= startY && mouseY <= endY;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        this.lastMouseX = (int) mouseX;
        this.lastMouseY = (int) mouseY;

        int posX = (width - SCREEN_WIDTH) / 2;
        int posY = (height - SCREEN_HEIGHT) / 2;

        if (this.context != null) {
            int dropdownX = context.xPosition;
            int dropdownY = context.yPosition;
            if (isMouseInside((int) mouseX, (int) mouseY, dropdownX, dropdownY, dropdownX + context.width, dropdownY + context.height)) {
                this.context.handleMouseClick((int) mouseX, (int) mouseY, mouseButton);
                return false;
            } else {
                this.context = null;
            }
        }

        this.bar.handleClick(this, posX, posY + SCREEN_HEIGHT - TaskBar.BAR_HEIGHT, (int) mouseX, (int) mouseY, mouseButton);

        for (int i = 0; i < windows.length; i++) {
            Window<Application> window = (Window<Application>) windows[i];
            if (window != null) {
                Window<Dialog> dialogWindow = window.getContent().getActiveDialog();
                if (isMouseWithinWindow((int) mouseX, (int) mouseY, window) || isMouseWithinWindow((int) mouseX, (int) mouseY, dialogWindow)) {
                    windows[i] = null;
                    updateWindowStack();
                    windows[0] = window;

                    windows[0].handleMouseClick(this, posX, posY, (int) mouseX, (int) mouseY, mouseButton);

                    if (isMouseWithinWindowBar((int) mouseX, (int) mouseY, dialogWindow)) {
                        this.dragging = true;
                        return false;
                    }

                    if (isMouseWithinWindowBar((int) mouseX, (int) mouseY, window) && dialogWindow == null) {
                        this.dragging = true;
                        return false;
                    }
                    break;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        this.dragging = false;
        if (this.context != null) {
            int dropdownX = context.xPosition;
            int dropdownY = context.yPosition;
            if (isMouseInside((int) mouseX, (int) mouseY, dropdownX, dropdownY, dropdownX + context.width, dropdownY + context.height)) {
                this.context.handleMouseRelease((int) mouseX, (int) mouseY, state);
            }
        } else if (windows[0] != null) {
            windows[0].handleMouseRelease((int) mouseX, (int) mouseY, state);
        }
        return true;
    }

    @Override
    public void afterKeyboardAction() {
//        if (Keyboard.getEventKeyState()) {
//            char pressed = Keyboard.getEventCharacter();
//            int code = Keyboard.getEventKey();
//
//            if (windows[0] != null) {
//                windows[0].handleKeyTyped(pressed, code);
//            }
//
////            super.charTyped(pressed, code);
//        } else {
//        }

        // Todo - handle key presses
//        this.minecraft.dispatchKeypresses();
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        boolean override = super.charTyped(codePoint, modifiers);
        if (!override && windows[0] != null)
            windows[0].handleCharTyped(codePoint, modifiers);
        return override;
    }

    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        final boolean override = super.keyPressed(keyCode, scanCode, modifiers);

        if (!pressed.contains(keyCode) && !override && windows[0] != null) {
            windows[0].handleKeyPressed(keyCode, scanCode, modifiers);
        }
        pressed.add(keyCode);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        pressed.remove(keyCode);

        boolean b = super.keyReleased(keyCode, scanCode, modifiers);

        if (keyCode >= 32 && keyCode < 256 && windows[0] != null) {
            windows[0].handleKeyReleased(keyCode, scanCode, modifiers);
            return true;
        }
        return b;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        int posX = (width - SCREEN_WIDTH) / 2;
        int posY = (height - SCREEN_HEIGHT) / 2;

        if (this.context != null) {
            int dropdownX = context.xPosition;
            int dropdownY = context.yPosition;
            if (isMouseInside((int) mouseX, (int) mouseY, dropdownX, dropdownY, dropdownX + context.width, dropdownY + context.height)) {
                this.context.handleMouseDrag((int) mouseX, (int) mouseY, button);
            }
            return true;
        }

        if (windows[0] != null) {
            Window<Application> window = (Window<Application>) windows[0];
            Window<Dialog> dialogWindow = window.getContent().getActiveDialog();
            if (dragging) {
                if (isMouseOnScreen((int) mouseX, (int) mouseY)) {
                    Objects.requireNonNullElse(dialogWindow, window).handleWindowMove(posX, posY, (int) -(lastMouseX - mouseX), (int) -(lastMouseY - mouseY));
                } else {
                    dragging = false;
                }
            } else {
                if (isMouseWithinWindow((int) mouseX, (int) mouseY, window) || isMouseWithinWindow((int) mouseX, (int) mouseY, dialogWindow)) {
                    window.handleMouseDrag((int) mouseX, (int) mouseY, button);
                }
            }
        }
        this.lastMouseX = (int) mouseX;
        this.lastMouseY = (int) mouseY;
        return true;
    }

    @Override
    public void mouseMoved(double pMouseX, double pMouseY) {

    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (delta != 0) {
            if (windows[0] != null) {
                windows[0].handleMouseScroll((int) mouseX, (int) mouseY, delta >= 0);
            }
        }
        return true;
    }

    @Override
    public void renderComponentTooltip(@NotNull PoseStack pose, @NotNull List<Component> tooltips, int x, int y) {
        super.renderComponentTooltip(pose, tooltips, x, y);
    }

    public Pair<Application, Boolean> sendApplicationToFront(AppInfo info) {
        for (int i = 0; i < windows.length; i++) {
            Window<?> window = windows[i];
            if (window != null && window.content instanceof Application && ((Application) window.content).getInfo() == info) {
                windows[i] = null;
                updateWindowStack();
                windows[0] = window;
                return Pair.of((Application) window.content, true);
            }
        }
        return Pair.of(null, false);
    }

    @Override
    public Application openApplication(AppInfo info) {
        return openApplication(info, (CompoundTag) null);
    }

    @Override
    public Application openApplication(AppInfo info, CompoundTag intentTag) {
        Optional<Application> optional = APPLICATIONS.stream().filter(app -> app.getInfo() == info).findFirst();
        Application[] a = new Application[]{null};
        optional.ifPresent(application -> a[0] = openApplication(application, intentTag));
        return a[0];
    }

    private Application openApplication(Application app, CompoundTag intent) {
        if (isApplicationNotInstalled(app.getInfo()))
            return null;

        if (isInvalidApplication(app.getInfo()))
            return null;

        var q = sendApplicationToFront(app.getInfo());
        if (q.right())
                return q.left();

        Window<Application> window = new Window<>(app, this);
        window.init((width - SCREEN_WIDTH) / 2, (height - SCREEN_HEIGHT) / 2, intent);

        if (appData.contains(app.getInfo().getFormattedId())) {
            app.load(appData.getCompound(app.getInfo().getFormattedId()));
        }

        if (app instanceof SystemApplication) {
            ((SystemApplication) app).setLaptop(this);
        }

        if (app instanceof SystemAccessor) {
            ((SystemAccessor) app).sendSystem(this);
        }

        if (app.getCurrentLayout() == null) {
            app.restoreDefaultLayout();
        }

        addWindow(window);

        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1f));
        return app;
    }

    @Override
    public Pair<Application, Boolean> openApplication(AppInfo info, File file) {
        if (isApplicationNotInstalled(info))
            return Pair.of(null, false);

        if (isInvalidApplication(info))
            return Pair.of(null, false);

        Optional<Application> optional = APPLICATIONS.stream().filter(app -> app.getInfo() == info).findFirst();
        if (optional.isPresent()) {
            Application application = optional.get();
            boolean alreadyRunning = isApplicationRunning(info);
            openApplication(application, null);
            if (isApplicationRunning(info)) {
                if (!application.handleFile(file)) {
                    if (!alreadyRunning) {
                        closeApplication(application);
                    }
                    return Pair.of(application, false);
                }
                return Pair.of(application, true);
            }
        }
        return Pair.of(null, true);
    }

    @Override
    public void closeApplication(AppInfo info) {
        Optional<Application> optional = APPLICATIONS.stream().filter(app -> app.getInfo() == info).findFirst();
        optional.ifPresent(this::closeApplication);
    }

    @SuppressWarnings("unchecked")
    private void closeApplication(Application app) {
        for (int i = 0; i < windows.length; i++) {
            Window<Application> window = (Window<Application>) windows[i];
            if (window != null) {
                if (window.content.getInfo().equals(app.getInfo())) {
                    if (app.isDirty()) {
                        CompoundTag container = new CompoundTag();
                        app.save(container);
                        app.clean();
                        appData.put(app.getInfo().getFormattedId(), container);
                        TaskManager.sendTask(new TaskUpdateApplicationData(pos.getX(), pos.getY(), pos.getZ(), app.getInfo().getFormattedId(), container));
                    }

                    if (app instanceof SystemApplication) {
                        ((SystemApplication) app).setLaptop(null);
                    }

                    window.handleClose();
                    windows[i] = null;
                    return;
                }
            }
        }
    }

    private void addWindow(Window<Application> window) {
        if (hasReachedWindowLimit())
            return;

        updateWindowStack();
        windows[0] = window;
    }

    private void updateWindowStack() {
        for (int i = windows.length - 1; i >= 0; i--) {
            if (windows[i] != null) {
                if (i + 1 < windows.length) {
                    if (i == 0 || windows[i - 1] != null) {
                        if (windows[i + 1] == null) {
                            windows[i + 1] = windows[i];
                            windows[i] = null;
                        }
                    }
                }
            }
        }
    }

    private boolean hasReachedWindowLimit() {
        for (Window<?> window : windows) {
            if (window == null) return false;
        }
        return true;
    }

    private boolean isMouseOnScreen(int mouseX, int mouseY) {
        int posX = (width - SCREEN_WIDTH) / 2;
        int posY = (height - SCREEN_HEIGHT) / 2;
        return isMouseInside(mouseX, mouseY, posX, posY, posX + SCREEN_WIDTH, posY + SCREEN_HEIGHT);
    }

    private boolean isMouseWithinWindowBar(int mouseX, int mouseY, Window<?> window) {
        if (window == null) return false;
        int posX = (width - SCREEN_WIDTH) / 2;
        int posY = (height - SCREEN_HEIGHT) / 2;
        return isMouseInside(mouseX, mouseY, posX + window.offsetX + 1, posY + window.offsetY + 1, posX + window.offsetX + window.width - 13, posY + window.offsetY + 11);
    }

    private boolean isMouseWithinWindow(int mouseX, int mouseY, Window<?> window) {
        if (window == null) return false;
        int posX = (width - SCREEN_WIDTH) / 2;
        int posY = (height - SCREEN_HEIGHT) / 2;
        return isMouseInside(mouseX, mouseY, posX + window.offsetX, posY + window.offsetY, posX + window.offsetX + window.width, posY + window.offsetY + window.height);
    }

    public boolean isMouseWithinApp(int mouseX, int mouseY, Window<?> window) {
        int posX = (width - SCREEN_WIDTH) / 2;
        int posY = (height - SCREEN_HEIGHT) / 2;
        return isMouseInside(mouseX, mouseY, posX + window.offsetX + 1, posY + window.offsetY + 13, posX + window.offsetX + window.width - 1, posY + window.offsetY + window.height - 1);
    }

    public boolean isApplicationRunning(AppInfo info) {
        for (Window<?> window : windows) {
            if (window != null && ((Application) window.content).getInfo() == info) {
                return true;
            }
        }
        return false;
    }

    public void nextWallpaper() {
        if (currentWallpaper + 1 < WALLPAPERS.size()) {
            currentWallpaper++;
        }
    }

    public void prevWallpaper() {
        if (currentWallpaper - 1 >= 0) {
            currentWallpaper--;
        }
    }

    public int getCurrentWallpaper() {
        return currentWallpaper;
    }

    public List<ResourceLocation> getWallapapers() {
        return ImmutableList.copyOf(WALLPAPERS);
    }

    @Nullable
    public Application getApplication(String appId) {
        return APPLICATIONS.stream().filter(app -> app.getInfo().getFormattedId().equals(appId)).findFirst().orElse(null);
    }

    @Override
    public List<AppInfo> getInstalledApplications() {
        return ImmutableList.copyOf(installedApps);
    }

    public boolean isApplicationInstalled(AppInfo info) {
        return info.isSystemApp() || installedApps.contains(info);
    }

    public boolean isApplicationNotInstalled(AppInfo info) {
        return !isApplicationInstalled(info);
    }

    private boolean isValidApplication(AppInfo info) {
        if (DevicesMod.getInstance().hasAllowedApplications()) {
            return DevicesMod.getInstance().getAllowedApplications().contains(info);
        }
        return true;
    }

    private boolean isInvalidApplication(AppInfo info) {
        return !isValidApplication(info);
    }

    public void installApplication(AppInfo info, @Nullable Callback<Object> callback) {
        if (isValidApplication(info)) {
            Task task = new TaskInstallApp(info, pos, true);
            task.setCallback((tag, success) ->
            {
                if (success) {
                    installedApps.add(info);
                    installedApps.sort(AppInfo.SORT_NAME);
                }
                if (callback != null) {
                    callback.execute(null, success);
                }
            });
            TaskManager.sendTask(task);
        }
    }

    public void removeApplication(AppInfo info, @Nullable Callback<Object> callback) {
        if (!isValidApplication(info))
            return;

        Task task = new TaskInstallApp(info, pos, false);
        task.setCallback((tag, success) ->
        {
            if (success) {
                installedApps.remove(info);
            }
            if (callback != null) {
                callback.execute(null, success);
            }
        });
        TaskManager.sendTask(task);
    }

    public List<Application> getApplications() {
        return APPLICATIONS;
    }

    public TaskBar getTaskBar() {
        return bar;
    }

    public Settings getSettings() {
        return settings;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void openContext(Layout layout, int x, int y) {
        layout.updateComponents(x, y);
        context = layout;
        layout.init();
    }

    @Override
    public boolean hasContext() {
        return context != null;
    }

    @Override
    public void closeContext() {
        context = null;
        dragging = false;
    }
}