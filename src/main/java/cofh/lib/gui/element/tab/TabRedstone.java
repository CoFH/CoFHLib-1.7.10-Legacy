package cofh.lib.gui.element.tab;

import cofh.api.tileentity.IRedstoneControl;
import cofh.lib.gui.GuiBase;
import cofh.lib.gui.GuiProps;
import cofh.lib.util.helpers.StringHelper;

import java.util.List;

import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class TabRedstone extends TabBase {

	public static boolean enable;
	public static int defaultSide = 1;
	public static int defaultHeaderColor = 0xe1c92f;
	public static int defaultSubHeaderColor = 0xaaafb8;
	public static int defaultTextColor = 0x000000;
	public static int defaultBackgroundColor = 0xd0230a;

	public static final ResourceLocation TAB_ICON = new ResourceLocation(GuiProps.PATH_ICONS + "icon_rs_control.png");

	IRedstoneControl myContainer;

	public TabRedstone(GuiBase gui, IRedstoneControl container) {

		this(gui, defaultSide, container);
	}

	public TabRedstone(GuiBase gui, int side, IRedstoneControl container) {

		super(gui, side);

		headerColor = defaultHeaderColor;
		subheaderColor = defaultSubHeaderColor;
		textColor = defaultTextColor;
		backgroundColor = defaultBackgroundColor;

		maxHeight = 92;
		maxWidth = 112;
		myContainer = container;
	}

	@Override
	public void addTooltip(List<String> list) {

		if (!isFullyOpened()) {
			if (myContainer.getControl().isDisabled()) {
				list.add(StringHelper.localize("info.cofh.disabled"));
				return;
			} else if (myContainer.getControl().isLow()) {
				list.add(StringHelper.localize("info.cofh.enabled") + ", " + StringHelper.localize("info.cofh.low"));
				return;
			}
			list.add(StringHelper.localize("info.cofh.enabled") + ", " + StringHelper.localize("info.cofh.high"));
			return;
		}
		int x = gui.getMouseX() - currentShiftX;
		int y = gui.getMouseY() - currentShiftY;
		if (28 <= x && x < 44 && 20 <= y && y < 36) {
			list.add(StringHelper.localize("info.cofh.ignored"));
		} else if (48 <= x && x < 64 && 20 <= y && y < 36) {
			list.add(StringHelper.localize("info.cofh.low"));
		} else if (68 <= x && x < 84 && 20 <= y && y < 36) {
			list.add(StringHelper.localize("info.cofh.high"));
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

		if (mouseX < 24 || mouseX >= 88 || mouseY < 16 || mouseY >= 40) {
			return false;
		}
		if (28 <= mouseX && mouseX < 44 && 20 <= mouseY && mouseY < 36) {
			if (!myContainer.getControl().isDisabled()) {
				myContainer.setControl(IRedstoneControl.ControlMode.DISABLED);
				GuiBase.playSound("random.click", 1.0F, 0.4F);
			}
		} else if (48 <= mouseX && mouseX < 64 && 20 <= mouseY && mouseY < 36) {
			if (!myContainer.getControl().isLow()) {
				myContainer.setControl(IRedstoneControl.ControlMode.LOW);
				GuiBase.playSound("random.click", 1.0F, 0.6F);
			}
		} else if (68 <= mouseX && mouseX < 84 && 20 <= mouseY && mouseY < 36) {
			if (!myContainer.getControl().isHigh()) {
				myContainer.setControl(IRedstoneControl.ControlMode.HIGH);
				GuiBase.playSound("random.click", 1.0F, 0.8F);
			}
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

		gui.drawTexturedModalRect(24, 16, 16, 20, 64, 24);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		if (myContainer.getControl().isDisabled()) {
			gui.drawIcon(GuiProps.ICON_BUTTON_HIGHLIGHT, 28, 20);
			gui.drawIcon(GuiProps.ICON_BUTTON, 48, 20);
			gui.drawIcon(GuiProps.ICON_BUTTON, 68, 20);
		} else {
			if (myContainer.getControl().isLow()) {
				gui.drawIcon(GuiProps.ICON_BUTTON, 28, 20);
				gui.drawIcon(GuiProps.ICON_BUTTON_HIGHLIGHT, 48, 20);
				gui.drawIcon(GuiProps.ICON_BUTTON, 68, 20);
			} else {
				gui.drawIcon(GuiProps.ICON_BUTTON, 28, 20);
				gui.drawIcon(GuiProps.ICON_BUTTON, 48, 20);
				gui.drawIcon(GuiProps.ICON_BUTTON_HIGHLIGHT, 68, 20);
			}
		}
		gui.drawIcon(GuiProps.ICON_RS_CONTROL_DISABLED, 28, 20);
		gui.drawIcon(GuiProps.ICON_RS_CONTROL_LOW, 48, 20);
		gui.drawIcon(GuiProps.ICON_RS_CONTROL_HIGH, 68, 20);
	}

	@Override
	protected void drawTabForeground() {

		drawTabIcon(TAB_ICON);
		if (!isFullyOpened()) {
			return;
		}
		getFontRenderer().drawStringWithShadow(StringHelper.localize("info.cofh.redstoneControl"), sideOffset() + 18, 6, headerColor);
		getFontRenderer().drawStringWithShadow(StringHelper.localize("info.cofh.controlStatus") + ":", sideOffset() + 6, 42, subheaderColor);
		getFontRenderer().drawStringWithShadow(StringHelper.localize("info.cofh.signalRequired") + ":", sideOffset() + 6, 66, subheaderColor);

		if (myContainer.getControl().isDisabled()) {
			getFontRenderer().drawString(StringHelper.localize("info.cofh.disabled"), sideOffset() + 14, 54, textColor);
			getFontRenderer().drawString(StringHelper.localize("info.cofh.ignored"), sideOffset() + 14, 78, textColor);
		} else {
			getFontRenderer().drawString(StringHelper.localize("info.cofh.enabled"), sideOffset() + 14, 54, textColor);
			if (myContainer.getControl().isLow()) {
				getFontRenderer().drawString(StringHelper.localize("info.cofh.low"), sideOffset() + 14, 78, textColor);
			} else {
				getFontRenderer().drawString(StringHelper.localize("info.cofh.high"), sideOffset() + 14, 78, textColor);
			}
		}
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

}
