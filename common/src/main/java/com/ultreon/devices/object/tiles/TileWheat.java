package com.ultreon.devices.object.tiles;


import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.devices.api.utils.RenderUtil;
import com.ultreon.devices.object.Game;

public class TileWheat extends Tile
{
	public TileWheat(int id, int x, int y)
	{
		super(id, x, y);
	}

	@Override
	public void render(PoseStack pose, Game game, int x, int y, Game.Layer layer)
	{
		RenderUtil.drawRectWithTexture(pose, game.xPosition + x * Tile.WIDTH, game.yPosition + y * Tile.HEIGHT - 6, this.x * 16, this.y * 16, WIDTH, HEIGHT + 1, 16, 16);
		RenderUtil.drawRectWithTexture(pose, game.xPosition + x * Tile.WIDTH, game.yPosition + y * Tile.HEIGHT - 2, this.x * 16, this.y * 16, WIDTH, HEIGHT + 1, 16, 16);
	}

	@Override
	public boolean isFullTile()
	{
		return false;
	}
}