package com.mrcrayfish.device.api.app.component;

import java.awt.Color;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.util.GuiHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import scala.actors.threadpool.Arrays;

/**
 * A component that allows you "access" to the players inventory. Now why access
 * is in quotes is because it's client side only. If you want to process anything,
 * you'll have to send the selected item slot to the server and process it there.
 * You can use a {@link Task} to perform this.
 * 
 * @author MrCrayfish
 */
public class Inventory extends Component
{
	protected static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
	
	protected static final int HIGHLIGHT = new Color(1F, 1F, 0F, 0.15F).getRGB();
	protected static final int COLOUR_MOUSE_OVER = new Color(1F, 1F, 1F, 0.15F).getRGB();
	
	protected int selected = -1;
	
	public Inventory(int left, int top)
	{
		super(left, top);
	}

	@Override
	public void render(Laptop laptop, Minecraft mc, int mouseX, int mouseY, boolean windowActive, float partialTicks)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
		GuiHelper.drawModalRectWithUV(xPosition, yPosition, 7, 139, 162, 54, 162, 54);

		InventoryPlayer inventory = mc.thePlayer.inventory;
		for(int i = 9; i < inventory.getSizeInventory() - 4; i++)
		{
			int offsetX = (i % 9) * 18;
			int offsetY = (i / 9) * 18 - 18;
			
			if(selected == i)
			{
				laptop.drawRect(xPosition + offsetX, yPosition + offsetY, xPosition + offsetX + 18, yPosition + offsetY + 18, HIGHLIGHT);
			}
			
			if(GuiHelper.isMouseInside(mouseX, mouseY, xPosition + offsetX, yPosition + offsetY, xPosition + offsetX + 17, yPosition + offsetY + 17))
			{
				laptop.drawRect(xPosition + offsetX, yPosition + offsetY, xPosition + offsetX + 18, yPosition + offsetY + 18, COLOUR_MOUSE_OVER);
			}
			
			ItemStack stack = inventory.getStackInSlot(i);
			if(stack != null)
			{
				RenderUtil.renderItem(xPosition + offsetX + 1, yPosition + offsetY + 1, stack, true);
			}
		}
	}
	
	@Override
	public void renderOverlay(Laptop laptop, Minecraft mc, int mouseX, int mouseY, boolean windowActive)
	{
		for(int i = 0; i < 3; i++)
		{
			for(int j = 0; j < 9; j++)
			{
				int x = xPosition + (j * 18) - 1;
				int y = yPosition + (i * 18) - 1;
				if(GuiHelper.isMouseInside(mouseX, mouseY, x, y, x + 18, y + 18))
				{
					ItemStack stack = mc.thePlayer.inventory.getStackInSlot((i * 9) + j + 9);
					if(stack != null)
					{
						laptop.drawHoveringText(Arrays.asList(new String[] { stack.getDisplayName() }), mouseX, mouseY);
					}
					return;
				}
			}
		}
	}
	
	@Override
	public void handleClick(int mouseX, int mouseY, int mouseButton)
	{
		for(int i = 0; i < 3; i++)
		{
			for(int j = 0; j < 9; j++)
			{
				int x = xPosition + (j * 18) - 1;
				int y = yPosition + (i * 18) - 1;
				if(GuiHelper.isMouseInside(mouseX, mouseY, x, y, x + 18, y + 18))
				{
					this.selected = (i * 9) + j + 9;
					return;
				}
			}
		}
	}
	
	public int getSelectedSlotIndex()
	{
		return selected;
	}
}