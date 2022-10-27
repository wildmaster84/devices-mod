package com.mrcrayfish.device.programs.system;

import com.mrcrayfish.device.api.ApplicationManager;
import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.*;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.Image;
import com.mrcrayfish.device.api.app.renderer.ItemRenderer;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.core.Settings;
import com.mrcrayfish.device.object.AppInfo;
import com.mrcrayfish.device.object.TrayItem;
import com.mrcrayfish.device.programs.system.component.Palette;
import com.mrcrayfish.device.programs.system.object.ColorScheme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

public class ApplicationSettings extends SystemApplication
{
	private Button buttonPrevious;

	private Layout layoutMain;
	private Layout layoutGeneral;
	private CheckBox checkBoxShowApps;

	private Layout layoutPersonalise;
	private Button buttonWallpaperLeft;
	private Button buttonWallpaperRight;
	private Button buttonWallpaperUrl;

	private Layout layoutColorScheme;
	private Button buttonColorSchemeApply;

	private Stack<Layout> predecessor = new Stack<>();

	@Override
	public void init(@Nullable NBTTagCompound intent)
	{
		buttonPrevious = new Button(2, 2, Icons.ARROW_LEFT);
		buttonPrevious.setVisible(false);
		buttonPrevious.setClickListener((mouseX, mouseY, mouseButton) ->
		{
			if(mouseButton == 0)
			{
				if(predecessor.size() > 0)
				{
					setCurrentLayout(predecessor.pop());
				}
				if(predecessor.isEmpty())
				{
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
			if(mouseButton == 0)
			{
				showMenu(layoutPersonalise = addWallpaperLayout());
			}
		});
		layoutMain.addComponent(buttonColorScheme);

		Button buttonReset = new Button(5, 100, "Reset Color Scheme");
		buttonReset.top = layoutMain.height - buttonReset.getHeight() - 5;
		buttonReset.setClickListener((mouseX, mouseY, mouseButton) ->
		{
			if(mouseButton == 0)
			{
				Laptop.getSystem().getSettings().getColorScheme().resetDefault();
			}
		});
		layoutMain.addComponent(buttonReset);

		layoutGeneral = new Menu("General");
		layoutGeneral.addComponent(buttonPrevious);

		checkBoxShowApps = new CheckBox("Show All Apps", 5, 5);
		checkBoxShowApps.setSelected(Settings.isShowAllApps());
		checkBoxShowApps.setClickListener((mouseX, mouseY, mouseButton) ->
		{
			Settings.setShowAllApps(checkBoxShowApps.isSelected());
			Laptop laptop = getLaptop();
			laptop.getTaskBar().setupApplications(laptop.getApplications());
		});
		layoutGeneral.addComponent(checkBoxShowApps);

		layoutPersonalise = null;

		layoutColorScheme = new Menu("UI Colors");

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
			if(mouseButton == 0)
			{
				ColorScheme colorScheme = Laptop.getSystem().getSettings().getColorScheme();
				colorScheme.setBackgroundColor(comboBoxHeaderColor.getValue());
				buttonColorSchemeApply.setEnabled(false);
			}
		});
		layoutColorScheme.addComponent(buttonColorSchemeApply);

		setCurrentLayout(layoutMain);
	}

	/**
	 * Create the layout for the wallpaper settings
	 *
	 * @return the layout.
	 */
	private Layout addWallpaperLayout() {
		// Create layout.
		Layout wallpaperLayout = new Menu("Wallpaper");

		// Wallpaper image.
		Image image = new Image(6, 29, 6+122, 29+70);
		image.setBorderThickness(1);
		image.setBorderVisible(true);
		assert getLaptop() != null;
		image.setImage(getLaptop().getCurrentWallpaper());
		wallpaperLayout.addComponent(image);

		// Previous wallpaper button.
		buttonWallpaperLeft = new Button(135, 27, Icons.ARROW_LEFT);
		buttonWallpaperLeft.setSize(25, 20);
		buttonWallpaperLeft.setClickListener((mouseX, mouseY, mouseButton) -> {
			if (mouseButton != 0)
				return;

			Laptop laptop = getLaptop();
			if (laptop != null) {
				laptop.prevWallpaper();
				image.setImage(getLaptop().getCurrentWallpaper());
			}
		});
		buttonWallpaperLeft.setEnabled(getLaptop().getCurrentWallpaper().isBuiltIn());
		wallpaperLayout.addComponent(buttonWallpaperLeft);

		// Next wallpaper button.
		buttonWallpaperRight = new Button(165, 27, Icons.ARROW_RIGHT);
		buttonWallpaperRight.setSize(25, 20);
		buttonWallpaperRight.setClickListener((mouseX, mouseY, mouseButton) -> {
			if (mouseButton != 0)
				return;

			Laptop laptop = getLaptop();
			if (laptop != null) {
				laptop.nextWallpaper();
				image.setImage(getLaptop().getCurrentWallpaper());
			}
		});
		buttonWallpaperRight.setEnabled(getLaptop().getCurrentWallpaper().isBuiltIn());
		wallpaperLayout.addComponent(buttonWallpaperRight);

		// Reset wallpaper button.
		Button resetWallpaperBtn = new Button(6, 100, "Reset Wallpaper");
		resetWallpaperBtn.setClickListener((mouseX, mouseY, mouseButton) -> {
			if (mouseButton == 0) {
				getLaptop().setWallpaper(0);
				image.setImage(getLaptop().getCurrentWallpaper());
				buttonWallpaperLeft.setEnabled(getLaptop().getCurrentWallpaper().isBuiltIn());
				buttonWallpaperRight.setEnabled(getLaptop().getCurrentWallpaper().isBuiltIn());
			}
		});
		resetWallpaperBtn.top = wallpaperLayout.height - resetWallpaperBtn.getHeight() - 5;
		wallpaperLayout.addComponent(resetWallpaperBtn);

		// Add back button.
		wallpaperLayout.addComponent(buttonPrevious);

		// Add wallpaper load from url button.
		buttonWallpaperUrl = new Button(135, 52, "Load", Icons.EARTH);
		buttonWallpaperUrl.setSize(55, 20);
		buttonWallpaperUrl.setClickListener((mouseX, mouseY, mouseButton) -> {
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
		wallpaperLayout.addComponent(buttonWallpaperUrl);
		Text wallpaperText = new Text("Wallpaper", image.left+3, image.top+3, image.componentWidth-6);
		wallpaperText.setShadow(true);
		wallpaperText.setTextColor(new Color(getLaptop().getSettings().getColorScheme().getTextColor()));
		wallpaperLayout.addComponent(wallpaperText);


		wallpaperLayout.addComponent(buttonPrevious);

		return wallpaperLayout;
	}

	@Override
	public void load(NBTTagCompound tagCompound)
	{

	}

	@Override
	public void save(NBTTagCompound tagCompound)
	{

	}

	private void showMenu(Layout layout)
	{
		predecessor.push(getCurrentLayout());
		buttonPrevious.setVisible(true);
		setCurrentLayout(layout);
	}

	@Override
	public void onClose()
	{
		super.onClose();
		predecessor.clear();
	}

	private static class Menu extends Layout
	{
		private String title;

		public Menu(String title)
		{
			super(200, 150);
			this.title = title;
		}

		@Override
		public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks)
		{
			Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getHeaderColor());
			Gui.drawRect(x, y, x + width, y + 20, color.getRGB());
			Gui.drawRect(x, y + 20, x + width, y + 21, color.darker().getRGB());
			mc.fontRenderer.drawString(title, x + 22, y + 6, Color.WHITE.getRGB(), true);
			super.render(laptop, mc, x, y, mouseX, mouseY, windowActive, partialTicks);
		}
	}

	public ComboBox.Custom<Integer> createColorPicker(int left, int top)
	{
		ComboBox.Custom<Integer> colorPicker = new ComboBox.Custom<>(left, top, 50, 100, 100);
		colorPicker.setValue(Color.RED.getRGB());
		colorPicker.setItemRenderer(new ItemRenderer<Integer>()
		{
			@Override
			public void render(Integer integer, Gui gui, Minecraft mc, int x, int y, int width, int height)
			{
				if(integer != null)
				{
					Gui.drawRect(x, y, x + width, y + height, integer);
				}
			}
		});
		colorPicker.setChangeListener((oldValue, newValue) ->
		{
			buttonColorSchemeApply.setEnabled(true);
		});

		Palette palette = new Palette(5, 5, colorPicker);
		Layout layout = colorPicker.getLayout();
		layout.addComponent(palette);

		return colorPicker;
	}

	public static class SettingsTrayItem extends TrayItem
	{
		public SettingsTrayItem()
		{
			super(Icons.WRENCH);
		}

		@Override
		public void handleClick(int mouseX, int mouseY, int mouseButton)
		{
			AppInfo info = ApplicationManager.getApplication("cdm:settings");
			if(info != null)
			{
				Laptop.getSystem().openApplication(info);
			}
		}
	}
}
