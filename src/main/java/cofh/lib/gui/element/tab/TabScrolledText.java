package cofh.lib.gui.element.tab;

import cofh.lib.gui.GuiBase;
import cofh.lib.gui.GuiProps;
import cofh.lib.util.helpers.MathHelper;

import java.util.List;

import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public abstract class TabScrolledText extends TabBase {

	protected List<String> myText;
	protected int firstLine = 0;
	protected int maxFirstLine = 0;
	protected int numLines = 0;

	public TabScrolledText(GuiBase gui, int side, String infoString) {

		super(gui, side);

		maxHeight = 92;
		myText = getFontRenderer().listFormattedStringToWidth(infoString, maxWidth - 16);
		numLines = Math.min(myText.size(), (maxHeight - 24) / getFontRenderer().FONT_HEIGHT);
		maxFirstLine = myText.size() - numLines;
	}

	public abstract ResourceLocation getTabIcon();

	public abstract String getTitle();

	@Override
	protected void drawTabForeground() {

		drawTabIcon(getTabIcon());
		if (!isFullyOpened()) {
			return;
		}
		if (firstLine > 0) {
			gui.drawIcon(GuiProps.ICON_ARROW_UP_ACTIVE, sideOffset() + maxWidth - 20, 16);
		} else {
			gui.drawIcon(GuiProps.ICON_ARROW_UP_INACTIVE, sideOffset() + maxWidth - 20, 16);
		}
		if (firstLine < maxFirstLine) {
			gui.drawIcon(GuiProps.ICON_ARROW_DOWN_ACTIVE, sideOffset() + maxWidth - 20, 76);
		} else {
			gui.drawIcon(GuiProps.ICON_ARROW_DOWN_INACTIVE, sideOffset() + maxWidth - 20, 76);
		}
		getFontRenderer().drawStringWithShadow(getTitle(), sideOffset() + 18, 6, headerColor);
		for (int i = firstLine; i < firstLine + numLines; i++) {
			getFontRenderer().drawString(myText.get(i), sideOffset() + 2, 20 + (i - firstLine) * getFontRenderer().FONT_HEIGHT, textColor);
		}
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void addTooltip(List<String> list) {

		if (!isFullyOpened()) {
			list.add(getTitle());
			return;
		}
	}

	@Override
	public boolean onMousePressed(int mouseX, int mouseY, int mouseButton) {

		int shiftedMouseX = mouseX - this.posX();
		int shiftedMouseY = mouseY - this.posY;

		if (!isFullyOpened()) {
			return false;
		}

		if (shiftedMouseX < 108) {
			return super.onMousePressed(mouseX, mouseY, mouseButton);
		}

		if (shiftedMouseY < 52) {
			firstLine = MathHelper.clamp(firstLine - 1, 0, maxFirstLine);
		} else {
			firstLine = MathHelper.clamp(firstLine + 1, 0, maxFirstLine);
		}
		return true;
	}

	@Override
	public boolean onMouseWheel(int mouseX, int mouseY, int movement) {

		if (!isFullyOpened()) {
			return false;
		}
		if (movement > 0) {
			firstLine = MathHelper.clamp(firstLine - 1, 0, maxFirstLine);
			return true;
		} else if (movement < 0) {
			firstLine = MathHelper.clamp(firstLine + 1, 0, maxFirstLine);
			return true;
		}
		return false;
	}

}
