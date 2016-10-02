package cofh.lib.gui.element.tab;

import cofh.api.tileentity.IReconfigurableFacing;
import cofh.api.tileentity.IReconfigurableSides;
import cofh.lib.gui.GuiBase;
import cofh.lib.gui.GuiProps;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.StringHelper;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class TabConfiguration extends TabBase {

	public static boolean enable;
	public static int defaultSide = 1;
	public static int defaultHeaderColor = 0xe1c92f;
	public static int defaultSubHeaderColor = 0xaaafb8;
	public static int defaultTextColor = 0x000000;
	public static int defaultBackgroundColor = 0x226688;

	public static final ResourceLocation TAB_ICON = new ResourceLocation(GuiProps.PATH_ICONS + "icon_config.png");

	IReconfigurableFacing myTile;
	IReconfigurableSides myTileSides;

	//ISidedTexture myTileTexture;

	public TabConfiguration(GuiBase gui, IReconfigurableFacing theTile) {

		this(gui, defaultSide, theTile);
	}

	public TabConfiguration(GuiBase gui, int side, IReconfigurableFacing theTile) {

		super(gui, side);

		headerColor = defaultHeaderColor;
		subheaderColor = defaultSubHeaderColor;
		textColor = defaultTextColor;
		backgroundColor = defaultBackgroundColor;

		maxHeight = 92;
		maxWidth = 100;
		myTile = theTile;
		myTileSides = (IReconfigurableSides) theTile;
		//myTileTexture = (ISidedTexture) theTile;
	}

	@Override
	public void addTooltip(List<String> list) {

		if (!isFullyOpened()) {
			list.add(StringHelper.localize("info.cofh.configuration"));
			return;
		}
	}

	@Override
	public boolean onMousePressed(int mouseX, int mouseY, int mouseButton) {

		if (!isFullyOpened()) {
			return false;
		}
		if (side == LEFT) {
			mouseX += currentWidth;
		}
		mouseX -= currentShiftX;
		mouseY -= currentShiftY;

		if (mouseX < 16 || mouseX >= 80 || mouseY < 20 || mouseY >= 84) {
			return false;
		}
		if (40 <= mouseX && mouseX < 56 && 24 <= mouseY && mouseY < 40) {
			handleSideChange(BlockHelper.SIDE_ABOVE[myTile.getFacing()], mouseButton);
		} else if (20 <= mouseX && mouseX < 36 && 44 <= mouseY && mouseY < 60) {
			handleSideChange(BlockHelper.SIDE_LEFT[myTile.getFacing()], mouseButton);
		} else if (40 <= mouseX && mouseX < 56 && 44 <= mouseY && mouseY < 60) {
			handleSideChange(myTile.getFacing(), mouseButton);
		} else if (60 <= mouseX && mouseX < 76 && 44 <= mouseY && mouseY < 60) {
			handleSideChange(BlockHelper.SIDE_RIGHT[myTile.getFacing()], mouseButton);
		} else if (40 <= mouseX && mouseX < 56 && 64 <= mouseY && mouseY < 80) {
			handleSideChange(BlockHelper.SIDE_BELOW[myTile.getFacing()], mouseButton);
		} else if (60 <= mouseX && mouseX < 76 && 64 <= mouseY && mouseY < 80) {
			handleSideChange(BlockHelper.SIDE_OPPOSITE[myTile.getFacing()], mouseButton);
		}
		return true;
	}

	@Override
	protected void drawTabBackground() {

		super.drawTabBackground();

		if (!isFullyOpened()) {
			return;
		}
		float colorR = (backgroundColor >> 16 & 255) / 255.0F * 0.6F;
		float colorG = (backgroundColor >> 8 & 255) / 255.0F * 0.6F;
		float colorB = (backgroundColor & 255) / 255.0F * 0.6F;
		GL11.glColor4f(colorR, colorG, colorB, 1.0F);
		gui.drawTexturedModalRect(16, 20, 16, 20, 64, 64);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	protected void drawTabForeground() {

		drawTabIcon(TAB_ICON);
		if (!isFullyOpened()) {
			return;
		}
		getFontRenderer().drawStringWithShadow(StringHelper.localize("info.cofh.configuration"), sideOffset() + 18, 6, headerColor);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		gui.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		//		for (int i = 0; i < 2; i++) {
		//			gui.drawIcon(myTileTexture.getTexture(BlockHelper.ENUM_SIDE_ABOVE[myTile.getFacing()], i), posX() + 40, 24);
		//			gui.drawIcon(myTileTexture.getTexture(BlockHelper.ENUM_SIDE_LEFT[myTile.getFacing()], i), posX() + 20, 44);
		//			gui.drawIcon(myTileTexture.getTexture(EnumFacing.VALUES[myTile.getFacing()], i), posX() + 40, 44);
		//			gui.drawIcon(myTileTexture.getTexture(BlockHelper.ENUM_SIDE_RIGHT[myTile.getFacing()], i), posX() + 60, 44);
		//			gui.drawIcon(myTileTexture.getTexture(BlockHelper.ENUM_SIDE_BELOW[myTile.getFacing()], i), posX() + 40, 64);
		//			gui.drawIcon(myTileTexture.getTexture(BlockHelper.ENUM_SIDE_OPPOSITE[myTile.getFacing()], i), posX() + 60, 64);
		//		}
		GL11.glDisable(GL11.GL_BLEND);
	}

	void handleSideChange(int side, int mouseButton) {

		if (GuiScreen.isShiftKeyDown()) {
			if (side == myTile.getFacing()) {
				if (myTileSides.resetSides()) {
					GuiBase.playSound("random.click", 1.0F, 0.2F);
				}
			} else if (myTileSides.setSide(EnumFacing.VALUES[side], 0)) {
				GuiBase.playSound("random.click", 1.0F, 0.4F);
			}
			return;
		}
		if (mouseButton == 0) {
			if (myTileSides.incrSide(EnumFacing.VALUES[side])) {
				GuiBase.playSound("random.click", 1.0F, 0.8F);
			}
		} else if (mouseButton == 1) {
			if (myTileSides.decrSide(EnumFacing.VALUES[side])) {
				GuiBase.playSound("random.click", 1.0F, 0.6F);
			}
		}
	}

}
