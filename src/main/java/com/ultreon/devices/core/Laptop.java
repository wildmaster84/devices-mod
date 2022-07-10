package com.ultreon.devices.core;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.devices.DevicesMod;
import com.ultreon.devices.Reference;
import com.ultreon.devices.api.ApplicationManager;
import com.ultreon.devices.api.app.Application;
import com.ultreon.devices.api.app.Dialog;
import com.ultreon.devices.api.app.Layout;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

//TODO Intro message (created by mrcrayfish, donate here)

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

    private static System system;
    private static BlockPos pos;
    private static Drive mainDrive;
    private static final List<Runnable> tasks = new CopyOnWriteArrayList<>();
    protected List<AppInfo> installedApps = new ArrayList<>();
    private final Settings settings;
    private final TaskBar bar;
    private final com.ultreon.devices.core.Window<?>[] windows;
    private Layout context = null;
    private final CompoundTag appData;
    private final CompoundTag systemData;
    private int currentWallpaper;
    private int lastMouseX, lastMouseY;
    private boolean dragging = false;

    public Laptop(LaptopBlockEntity laptop) {
        super(new TextComponent("Laptop"));
        this.appData = laptop.getApplicationData();
        this.systemData = laptop.getSystemData();
        this.windows = new com.ultreon.devices.core.Window[5];
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

    public static void runLater(Runnable task) {
        tasks.add(task);
    }

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
        for (com.ultreon.devices.core.Window<?> window : windows) {
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

    @Override
    public void resize(@NotNull Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
        for (com.ultreon.devices.core.Window<?> window : windows) {
            if (window != null) {
                window.content.markForLayoutUpdate();
            }
        }
    }

    @Override
    public void tick() {
        bar.onTick();

        for (com.ultreon.devices.core.Window<?> window : windows) {
            if (window != null) {
                window.onTick();
            }
        }

        FileBrowser.refreshList = false;
    }

    @Override
    public void render(@NotNull PoseStack pose, int mouseX, int mouseY, float partialTicks) {
        for (Runnable task : tasks) {
            task.run();
        }
        tasks.clear();

        //Fixes the strange partialTicks that Forge decided to give us
        partialTicks = Minecraft.getInstance().getFrameTime();

        this.renderBackground(pose);

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, LAPTOP_GUI);

        /* Physical Screen */
        int posX = (width - DEVICE_WIDTH) / 2;
        int posY = (height - DEVICE_HEIGHT) / 2;

        /* Corners */
        blit(pose, posX, posY, 0, 0, BORDER, BORDER); // TOP-LEFT
        blit(pose, posX + DEVICE_WIDTH - BORDER, posY, 11, 0, BORDER, BORDER); // TOP-RIGHT
        blit(pose, posX + DEVICE_WIDTH - BORDER, posY + DEVICE_HEIGHT - BORDER, 11, 11, BORDER, BORDER); // BOTTOM-RIGHT
        blit(pose, posX, posY + DEVICE_HEIGHT - BORDER, 0, 11, BORDER, BORDER); // BOTTOM-LEFT

        /* Edges */
        Gui.blit(pose, posX + BORDER, posY, SCREEN_WIDTH, BORDER, 10, 0, 1, BORDER, 256, 256); // TOP
        Gui.blit(pose, posX + DEVICE_WIDTH - BORDER, posY + BORDER, BORDER, SCREEN_HEIGHT, 11, 10, BORDER, 1, 256, 256); // RIGHT
        Gui.blit(pose, posX + BORDER, posY + DEVICE_HEIGHT - BORDER, SCREEN_WIDTH, BORDER, 10, 11, 1, BORDER, 256, 256); // BOTTOM
        Gui.blit(pose, posX, posY + BORDER, BORDER, SCREEN_HEIGHT, 0, 11, BORDER, 1, 256, 256); // LEFT

        /* Center */
        Gui.blit(pose, posX + BORDER, posY + BORDER, SCREEN_WIDTH, SCREEN_HEIGHT, 10, 10, 1, 1, 256, 256);

        /* Wallpaper */
        RenderSystem.setShaderTexture(0, WALLPAPERS.get(currentWallpaper));
        RenderUtil.drawRectWithTexture(pose, posX + 10, posY + 10, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, 512, 288);

        if (!DevicesMod.DEVELOPER_MODE) {
            if (Reference.VERSION.contains("-dev")) {
                drawString(pose, font, "Dev Test v" + Reference.VERSION, posX + BORDER + 5, posY + BORDER + 5, Color.WHITE.getRGB());
            } else {
                drawString(pose, font, "Alpha v" + Reference.VERSION, posX + BORDER + 5, posY + BORDER + 5, Color.WHITE.getRGB());
            }
        } else {
            drawString(pose, font, "Developer Version - " + Reference.VERSION, posX + BORDER + 5, posY + BORDER + 5, Color.WHITE.getRGB());
        }

        boolean insideContext = false;
        if (context != null) {
            insideContext = isMouseInside(mouseX, mouseY, context.xPosition, context.yPosition, context.xPosition + context.width, context.yPosition + context.height);
        }

        Image.CACHE.forEach((s, cachedImage) -> cachedImage.delete());

        /* Window */
        for (int i = windows.length - 1; i >= 0; i--) {
            com.ultreon.devices.core.Window<?> window = windows[i];
            if (window != null) {
                window.render(pose, this, minecraft, posX + BORDER, posY + BORDER, mouseX, mouseY, i == 0 && !insideContext, partialTicks);
            }
        }

        /* Application Bar */
        bar.render(pose, this, minecraft, posX + 10, posY + DEVICE_HEIGHT - 28, mouseX, mouseY, partialTicks);

        if (context != null) {
            context.render(pose, this, minecraft, context.xPosition, context.yPosition, mouseX, mouseY, true, partialTicks);
        }

        Image.CACHE.entrySet().removeIf(entry ->
        {
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

        super.render(pose, mouseX, mouseY, partialTicks);
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
            com.ultreon.devices.core.Window<Application> window = (com.ultreon.devices.core.Window<Application>) windows[i];
            if (window != null) {
                com.ultreon.devices.core.Window<Dialog> dialogWindow = window.getContent().getActiveDialog();
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
            windows[0].handleKeyTyped(codePoint, modifiers);
        return override;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        boolean b = super.keyReleased(keyCode, scanCode, modifiers);

        if (keyCode >= 32 && keyCode < 256 && windows[0] != null) {
            windows[0].handleKeyReleased((char) InputConstants.getKey(keyCode, scanCode).getValue(), keyCode);
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
            com.ultreon.devices.core.Window<Application> window = (com.ultreon.devices.core.Window<Application>) windows[0];
            com.ultreon.devices.core.Window<Dialog> dialogWindow = window.getContent().getActiveDialog();
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

    public boolean sendApplicationToFront(AppInfo info) {
        for (int i = 0; i < windows.length; i++) {
            com.ultreon.devices.core.Window<?> window = windows[i];
            if (window != null && window.content instanceof Application && ((Application) window.content).getInfo() == info) {
                windows[i] = null;
                updateWindowStack();
                windows[0] = window;
                return true;
            }
        }
        return false;
    }

    @Override
    public void openApplication(AppInfo info) {
        openApplication(info, (CompoundTag) null);
    }

    @Override
    public void openApplication(AppInfo info, CompoundTag intentTag) {
        Optional<Application> optional = APPLICATIONS.stream().filter(app -> app.getInfo() == info).findFirst();
        optional.ifPresent(application -> openApplication(application, intentTag));
    }

    private void openApplication(Application app, CompoundTag intent) {
        if (!isApplicationInstalled(app.getInfo()))
            return;

        if (!isValidApplication(app.getInfo()))
            return;

        if (sendApplicationToFront(app.getInfo()))
            return;

        com.ultreon.devices.core.Window<Application> window = new com.ultreon.devices.core.Window<>(app, this);
        window.init((width - SCREEN_WIDTH) / 2, (height - SCREEN_HEIGHT) / 2, intent);

        if (appData.contains(app.getInfo().getFormattedId())) {
            app.load(appData.getCompound(app.getInfo().getFormattedId()));
        }

        if (app instanceof SystemApplication) {
            ((SystemApplication) app).setLaptop(this);
        }

        if (app.getCurrentLayout() == null) {
            app.restoreDefaultLayout();
        }

        addWindow(window);

        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1f));
    }

    @Override
    public boolean openApplication(AppInfo info, File file) {
        if (!isApplicationInstalled(info))
            return false;

        if (!isValidApplication(info))
            return false;

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
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void closeApplication(AppInfo info) {
        Optional<Application> optional = APPLICATIONS.stream().filter(app -> app.getInfo() == info).findFirst();
        optional.ifPresent(this::closeApplication);
    }

    @SuppressWarnings("unchecked")
    private void closeApplication(Application app) {
        for (int i = 0; i < windows.length; i++) {
            com.ultreon.devices.core.Window<Application> window = (com.ultreon.devices.core.Window<Application>) windows[i];
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

    private void addWindow(com.ultreon.devices.core.Window<Application> window) {
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
        for (com.ultreon.devices.core.Window<?> window : windows) {
            if (window == null) return false;
        }
        return true;
    }

    private boolean isMouseOnScreen(int mouseX, int mouseY) {
        int posX = (width - SCREEN_WIDTH) / 2;
        int posY = (height - SCREEN_HEIGHT) / 2;
        return isMouseInside(mouseX, mouseY, posX, posY, posX + SCREEN_WIDTH, posY + SCREEN_HEIGHT);
    }

    private boolean isMouseWithinWindowBar(int mouseX, int mouseY, com.ultreon.devices.core.Window<?> window) {
        if (window == null) return false;
        int posX = (width - SCREEN_WIDTH) / 2;
        int posY = (height - SCREEN_HEIGHT) / 2;
        return isMouseInside(mouseX, mouseY, posX + window.offsetX + 1, posY + window.offsetY + 1, posX + window.offsetX + window.width - 13, posY + window.offsetY + 11);
    }

    private boolean isMouseWithinWindow(int mouseX, int mouseY, com.ultreon.devices.core.Window<?> window) {
        if (window == null) return false;
        int posX = (width - SCREEN_WIDTH) / 2;
        int posY = (height - SCREEN_HEIGHT) / 2;
        return isMouseInside(mouseX, mouseY, posX + window.offsetX, posY + window.offsetY, posX + window.offsetX + window.width, posY + window.offsetY + window.height);
    }

    public boolean isMouseWithinApp(int mouseX, int mouseY, com.ultreon.devices.core.Window<?> window) {
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

    private boolean isValidApplication(AppInfo info) {
        if (DevicesMod.getInstance().hasAllowedApplications()) {
            return DevicesMod.getInstance().getAllowedApplications().contains(info);
        }
        return true;
    }

    public void installApplication(AppInfo info, @Nullable Callback<Object> callback) {
        if (!isValidApplication(info))
            return;

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
