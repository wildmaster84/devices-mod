package com.ultreon.devices.programs.system;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.devices.api.ApplicationManager;
import com.ultreon.devices.api.app.Dialog;
import com.ultreon.devices.api.app.Icons;
import com.ultreon.devices.api.app.Layout;
import com.ultreon.devices.api.app.component.Button;
import com.ultreon.devices.api.app.component.CheckBox;
import com.ultreon.devices.api.app.component.ComboBox;
import com.ultreon.devices.api.app.renderer.ItemRenderer;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.core.Settings;
import com.ultreon.devices.object.AppInfo;
import com.ultreon.devices.object.TrayItem;
import com.ultreon.devices.programs.system.component.Palette;
import com.ultreon.devices.programs.system.object.ColorScheme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Stack;

@SuppressWarnings("FieldCanBeLocal")
public class SettingsApp extends SystemApp {
    private Button buttonPrevious;

    private Layout layoutMain;
    private Layout layoutGeneral;
    private CheckBox checkBoxShowApps;

    private Layout layoutPersonalise;
    private Layout layoutWallpaper;
    private Button buttonWallpaperLeft;
    private Button buttonWallpaperRight;
    private Button buttonWallpaperUrl;

    private Layout layoutColorScheme;
    private Button buttonColorSchemeApply;

    private final Stack<Layout> predecessor = new Stack<>();

    @Override
    public void init(@Nullable CompoundTag intent) {
        buttonPrevious = new Button(2, 2, Icons.ARROW_LEFT);
        buttonPrevious.setVisible(false);
        buttonPrevious.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            if (mouseButton == 0) {
                if (predecessor.size() > 0) {
                    setCurrentLayout(predecessor.pop());
                }
                if (predecessor.isEmpty()) {
                    buttonPrevious.setVisible(false);
                }
            }
        });

        layoutMain = new Menu("Home");

        Button buttonColorScheme = new Button(5, 26, "Personalise", Icons.EDIT);
        buttonColorScheme.setSize(90, 20);
        buttonColorScheme.setToolTip("Personalise", "Change the wallpaper, UI colors, and more!");
        buttonColorScheme.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            if (mouseButton == 0) {
                showMenu(layoutPersonalise);
            }
        });
        layoutMain.addComponent(buttonColorScheme);

        layoutGeneral = new Menu("General");
        layoutGeneral.addComponent(buttonPrevious);

        checkBoxShowApps = new CheckBox("Show All Apps", 5, 5);
        checkBoxShowApps.setSelected(Settings.isShowAllApps());
        checkBoxShowApps.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            Settings.setShowAllApps(checkBoxShowApps.isSelected());
            Laptop laptop = getLaptop();
            assert laptop != null;
            laptop.getTaskBar().setupApplications(laptop.getApplications());
        });
        layoutGeneral.addComponent(checkBoxShowApps);

        layoutPersonalise = new Menu("Personalise");
        layoutPersonalise.addComponent(buttonPrevious);
        layoutWallpaper = new Menu("Wallpaper");

        Button buttonWallpaper = new Button(5, 26, "Wallpaper", Icons.EDIT);
        buttonWallpaper.setSize(90, 20);
        //buttonWallpaper.top = this.getHeight()-buttonWallpaper.getHeight()-5;
        buttonWallpaper.setToolTip("Wallpaper", "Manage the wallpaper.");
        buttonWallpaper.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            if (mouseButton == 0) {
                showMenu(layoutWallpaper);
            }
        });
        layoutPersonalise.addComponent(buttonWallpaper);
        var image = new com.ultreon.devices.api.app.component.Image(6, 29, 6+122, 29+70);
        image.setBorderThickness(1);
        image.setBorderVisible(true);
        image.setImage(getLaptop().getCurrentWallpaper());
        layoutWallpaper.addComponent(image);


//        layoutPersonalise.setBackground((pose, gui, mc, x, y, width, height, mouseX, mouseY, windowActive) ->
//        {
//            int wallpaperX = 7;
//            int wallpaperY = 28;
//            assert getLaptop() != null;
//            Gui.fill(pose, x + wallpaperX - 1, y + wallpaperY - 1, x + wallpaperX - 1 + 122, y + wallpaperY - 1 + 70, getLaptop().getSettings().getColorScheme().getHeaderColor());
//            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
//            List<ResourceLocation> wallpapers = getLaptop().getWallapapers();
////            RenderSystem.setShaderTexture(0, wallpapers.get(getLaptop().getCurrentWallpaper()));
////            RenderUtil.drawRectWithFullTexture(pose, x + wallpaperX, y + wallpaperY, 0, 0, 120, 68);
//            mc.font.drawShadow(pose, "Wallpaper", x + wallpaperX + 3, y + wallpaperY + 3, getLaptop().getSettings().getColorScheme().getTextColor());
//        });

        buttonWallpaperLeft = new Button(135, 27, Icons.ARROW_LEFT);
        buttonWallpaperLeft.setSize(25, 20);
        buttonWallpaperLeft.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            if (mouseButton != 0)
                return;

            Laptop laptop = getLaptop();
            if (laptop != null) {
                laptop.prevWallpaper();
                image.setImage(getLaptop().getCurrentWallpaper());
            }
        });
        buttonWallpaperLeft.setEnabled(getLaptop().getCurrentWallpaper().isBuiltIn());
        layoutWallpaper.addComponent(buttonWallpaperLeft);

        buttonWallpaperRight = new Button(165, 27, Icons.ARROW_RIGHT);
        buttonWallpaperRight.setSize(25, 20);
        buttonWallpaperRight.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            if (mouseButton != 0)
                return;

            Laptop laptop = getLaptop();
            if (laptop != null) {
                laptop.nextWallpaper();
                image.setImage(getLaptop().getCurrentWallpaper());
            }
        });
        buttonWallpaperRight.setEnabled(getLaptop().getCurrentWallpaper().isBuiltIn());
        layoutWallpaper.addComponent(buttonWallpaperRight);

        Button resetWallpaper = new Button(6, 100, "Reset Wallpaper");
        resetWallpaper.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            if (mouseButton == 0) {
                getLaptop().setWallpaper(0);
                image.setImage(getLaptop().getCurrentWallpaper());
                buttonWallpaperLeft.setEnabled(getLaptop().getCurrentWallpaper().isBuiltIn());
                buttonWallpaperRight.setEnabled(getLaptop().getCurrentWallpaper().isBuiltIn());
            }
        });
        resetWallpaper.top = layoutWallpaper.height - resetWallpaper.getHeight() - 5;
        layoutWallpaper.addComponent(resetWallpaper);
        layoutWallpaper.addComponent(buttonPrevious);
//        var i = 0;
//        for (var wallpaper : Laptop.getWALLPAPERS()) {
//            var a = new Button(i%2==0?0:75, 20+(i/2)*30, 75, 30, wallpaper, 0, 0, 75, 30);
//            int finalI = i;
//            a.setClickListener(((mouseX, mouseY, mouseButton) -> {
//                assert getLaptop() != null;
//                getLaptop().setWallpaper(finalI);
//            }));
//            a.setIconU(512, 288);
//            a.setIconSource(512, 288);
//            layoutWallpaper.addComponent(a);
//            i++;
//        }
        buttonWallpaperUrl = new Button(135, 52, "Load", Icons.EARTH);
        buttonWallpaperUrl.setSize(55, 20);
        buttonWallpaperUrl.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            if (mouseButton != 0)
                return;

            Dialog.Input dialog = new Dialog.Input("Enter the URL of the image");
            dialog.setResponseHandler((success, string) -> {
                if (getLaptop() != null) {
                    getLaptop().setWallpaper(string);
                    image.setImage(getLaptop().getCurrentWallpaper());
                    buttonWallpaperLeft.setEnabled(getLaptop().getCurrentWallpaper().isBuiltIn());
                    buttonWallpaperRight.setEnabled(getLaptop().getCurrentWallpaper().isBuiltIn());
                }
                return success;
            });
            openDialog(dialog);
        });
        layoutWallpaper.addComponent(buttonWallpaperUrl);

        Button buttonReset = new Button(6, 100, "Reset Color Scheme");
        buttonReset.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            if (mouseButton == 0) {
                Laptop.getSystem().getSettings().getColorScheme().resetDefault();
            }
        });
        buttonReset.top = layoutPersonalise.height - buttonReset.getHeight() - 5;
        layoutPersonalise.addComponent(buttonReset);

        layoutColorScheme = new Menu("UI Colors");
        layoutPersonalise.addComponent(buttonPrevious);

        ComboBox.Custom<Integer> comboBoxTextColor = createColorPicker(145, 26);
        layoutColorScheme.addComponent(comboBoxTextColor);

        ComboBox.Custom<Integer> comboBoxTextSecondaryColor = createColorPicker(145, 44);
        layoutColorScheme.addComponent(comboBoxTextSecondaryColor);

        ComboBox.Custom<Integer> comboBoxHeaderColor = createColorPicker(145, 62);
        layoutColorScheme.addComponent(comboBoxHeaderColor);

        ComboBox.Custom<Integer> comboBoxBackgroundColor = createColorPicker(145, 80);
        layoutColorScheme.addComponent(comboBoxBackgroundColor);

        ComboBox.Custom<Integer> comboBoxBackgroundSecondaryColor = createColorPicker(145, 98);
        layoutColorScheme.addComponent(comboBoxBackgroundSecondaryColor);

        ComboBox.Custom<Integer> comboBoxItemBackgroundColor = createColorPicker(145, 116);
        layoutColorScheme.addComponent(comboBoxItemBackgroundColor);

        ComboBox.Custom<Integer> comboBoxItemHighlightColor = createColorPicker(145, 134);
        layoutColorScheme.addComponent(comboBoxItemHighlightColor);

        buttonColorSchemeApply = new Button(5, 79, Icons.CHECK);
        buttonColorSchemeApply.setEnabled(false);
        buttonColorSchemeApply.setToolTip("Apply", "Set these colors as the new color scheme");
        buttonColorSchemeApply.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            if (mouseButton == 0) {
                ColorScheme colorScheme = Laptop.getSystem().getSettings().getColorScheme();
                colorScheme.setBackgroundColor(comboBoxHeaderColor.getValue());
                buttonColorSchemeApply.setEnabled(false);
            }
        });
        layoutColorScheme.addComponent(buttonColorSchemeApply);

        setCurrentLayout(layoutMain);
    }

    @Override
    public void load(CompoundTag tag) {

    }

    @Override
    public void save(CompoundTag tag) {

    }

    private void showMenu(Layout layout) {
        predecessor.push(getCurrentLayout());
        buttonPrevious.setVisible(true);
        setCurrentLayout(layout);
    }

    @Override
    public void onClose() {
        super.onClose();
        predecessor.clear();
    }

    private static class Menu extends Layout {
        private final String title;

        public Menu(String title) {
            super(200, 150);
            this.title = title;
        }

        @Override
        public void render(PoseStack pose, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
            Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getHeaderColor());
            Gui.fill(pose, x, y, x + width, y + 20, color.getRGB());
            Gui.fill(pose, x, y + 20, x + width, y + 21, color.darker().getRGB());
            mc.font.drawShadow(pose, title, x + 22, y + 6, Color.WHITE.getRGB());
            super.render(pose, laptop, mc, x, y, mouseX, mouseY, windowActive, partialTicks);
        }
    }

    public ComboBox.Custom<Integer> createColorPicker(int left, int top) {
        ComboBox.Custom<Integer> colorPicker = new ComboBox.Custom<>(left, top, 50, 100, 100);
        colorPicker.setValue(Color.RED.getRGB());
        colorPicker.setItemRenderer(new ItemRenderer<>() {
            @Override
            public void render(PoseStack pose, Integer integer, GuiComponent gui, Minecraft mc, int x, int y, int width, int height) {
                if (integer != null) {
                    Gui.fill(pose, x, y, x + width, y + height, integer);
                }
            }
        });
        colorPicker.setChangeListener((oldValue, newValue) ->
                buttonColorSchemeApply.setEnabled(true));

        Palette palette = new Palette(5, 5, colorPicker);
        Layout layout = colorPicker.getLayout();
        layout.addComponent(palette);

        return colorPicker;
    }

    public static class SettingsTrayItem extends TrayItem {
        public SettingsTrayItem() {
            super(Icons.WRENCH);
        }

        @Override
        public void handleClick(int mouseX, int mouseY, int mouseButton) {
            AppInfo info = ApplicationManager.getApplication("devices:settings");
            if (info != null) {
                Laptop.getSystem().openApplication(info);
            }
        }
    }
}
