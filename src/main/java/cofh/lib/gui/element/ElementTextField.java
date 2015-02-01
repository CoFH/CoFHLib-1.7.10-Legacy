package cofh.lib.gui.element;

import cofh.lib.gui.GuiBase;
import cofh.lib.gui.GuiColor;
import cofh.lib.util.helpers.MathHelper;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;


public class ElementTextField extends ElementBase {

	public int borderColor       = new GuiColor( 55,  55,  55).getColor();
	public int backgroundColor   = new GuiColor(139, 139, 139).getColor();
	public int disabledColor     = new GuiColor(198, 198, 198).getColor();
	public int selectedLineColor = new GuiColor(160, 160, 224).getColor();
	public int textColor         = new GuiColor(224, 224, 224).getColor();
	public int selectedTextColor = new GuiColor(224, 224, 224).getColor();

	protected final char[] text;
	protected int textLength;
	protected int selectionStart, selectionEnd;
	protected int renderStart, caret;

	private boolean isFocused;
	private boolean canFocusChange = true;
	private boolean selecting;

	public ElementTextField(GuiBase gui, int posX, int posY, int width, int height) {

		this(gui, posX, posY, width, height, (short) 32);
	}

	public ElementTextField(GuiBase gui, int posX, int posY, int width, int height, short limit) {

		super(gui, posX, posY, width, height);
		text = new char[limit];
	}

	public ElementTextField setFocusable(boolean focusable) {

		canFocusChange = focusable;
		return this;
	}

	public ElementTextField setFocused(boolean focused) {

		if (canFocusChange)
			isFocused = focused;
		return this;
	}

	public boolean isFocused() {

		return isFocused;
	}

	public String getSelectedText() {

		if (selectionStart != selectionEnd) {
			return new String(text, selectionStart, selectionEnd);
		}
		return new String(text, 0, textLength);
	}

	protected void findRenderStart() {

		caret = MathHelper.clampI(caret, 0, textLength);
		if (caret < renderStart) {
			renderStart = caret;
			return;
		}

		FontRenderer font = getFontRenderer();
		int endX = sizeX - 1;

		for (int i = renderStart, width = 0; i < caret; ++i) {
			width += font.getCharWidth(text[i]);
			while (width > endX) {
				width -= font.getCharWidth(text[renderStart++]);
				if (renderStart >= textLength)
					return;
			}
		}
	}

	protected final int seekNextCaretLocation(int pos) {

		return seekNextCaretLocation(pos, true);
	}

	protected final int seekNextCaretLocation(int pos, boolean forward) {

		return 0;
	}

	@Override
	public boolean onKeyTyped(char charTyped, int keyTyped) {

		if (!isFocused())
			return false;

		switch (charTyped) {
		case 1: // ^A
			selectionEnd = textLength;
			renderStart = selectionStart = caret = 0;
			return true;
		case 3: // ^C
			if (selectionStart != selectionEnd) {
				GuiScreen.setClipboardString(getSelectedText());
			}
			return true;
		case 24: // ^X
			if (selectionStart != selectionEnd) {
				GuiScreen.setClipboardString(getSelectedText());
				if (++selectionEnd < textLength)
					System.arraycopy(text, selectionEnd, text, selectionStart, textLength - selectionEnd);
				textLength -= selectionEnd - selectionStart - 1;

				selectionEnd = caret = selectionStart;
				findRenderStart();
			}

			return true;
		case 22: // ^V
			// writeText(GuiScreen.getClipboardString());

			return true;
		default:
			switch (keyTyped) {
			case 14: // backspace
				if (GuiScreen.isCtrlKeyDown())
					;// deleteWords(-1);
				else
					;// deleteFromCursor(-1);

				return true;
			case 211: // delete
				if (GuiScreen.isCtrlKeyDown())
					;//deleteWords(1);
				else
					;//deleteFromCursor(1);

				return true;
			case 199: // home
				if (GuiScreen.isShiftKeyDown()) {
					if (caret >= selectionEnd)
						selectionEnd = selectionStart;
					selectionStart = 0;
				}
				renderStart = caret = 0;

				return true;
			case 207: // end
				if (GuiScreen.isShiftKeyDown()) {
					if (caret <= selectionStart)
						selectionStart = selectionEnd;
					selectionEnd = textLength;
				}
				caret = textLength;
				findRenderStart();

				return true;
			case 203: // left arrow
			case 205: // right arrow
				int size = keyTyped == 203 ? -1 : 1;
				if (GuiScreen.isCtrlKeyDown())
					size = seekNextCaretLocation(caret, keyTyped == 205) - caret;

				if (selectionStart == selectionEnd)
					selectionStart = selectionEnd = caret;

				caret = MathHelper.clampI(caret + size, 0, textLength);
				findRenderStart();

				if (GuiScreen.isShiftKeyDown()) {
					if (caret < selectionEnd)
						selectionStart = caret;
					else if (caret > selectionStart)
						selectionEnd = caret;

					if (selectionStart > selectionEnd) {
						int t = selectionStart;
						selectionStart = selectionEnd;
						selectionEnd = t;
					} else if (selectionEnd < selectionStart) {
						int t = selectionStart;
						selectionStart = selectionEnd;
						selectionEnd = t;
					}
				}

				return true;
			default:
				if (ChatAllowedCharacters.isAllowedCharacter(charTyped)) {
					if (textLength == text.length)
						return true;
					if (caret++ < textLength)
						System.arraycopy(text, caret - 1, text, caret, textLength - caret);
					text[caret - 1] = charTyped;
					return true;
				} else
					return false;
			}
		}
	}

	@Override
	public boolean onMousePressed(int mouseX, int mouseY, int mouseButton) {

		selecting = mouseButton == 0;
		if (selecting) {
			FontRenderer font = getFontRenderer();
			int pos = mouseX - posX - 1;
			for (int i = renderStart, width = 0; i < textLength; ++i) {
				int charW = font.getCharWidth(text[i]);
				if ((width += charW) > pos) {
					selectionStart = selectionEnd = caret = i;
					break;
				}
			}
		}

		setFocused(true);
		return true;
	}

	@Override
	public void update(int mouseX, int mouseY) {

		//if (selecting) {
		//	FontRenderer font = getFontRenderer();
		//	int pos = mouseX - posX - 1;
		//	for (int i = renderStart, width = 0; i < textLength; ++i) {
		//	}
		//}
	}

	@Override
	public void onMouseReleased(int mouseX, int mouseY) {

		selecting = false;
	}

	@Override
	public void drawBackground(int mouseX, int mouseY, float gameTicks) {

		drawModalRect(posX - 1, posY - 1, posX + sizeX + 1, posY + sizeY + 1, borderColor);
		drawModalRect(posX, posY, posX + sizeX, posY + sizeY, isEnabled() ? backgroundColor : disabledColor);
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {

		FontRenderer font = getFontRenderer();
		char[] text = this.text;
		int startX = posX + 1, endX = sizeX - 1, startY = posY + 1, endY = startY + font.FONT_HEIGHT;
		for (int i = renderStart, width = 0; i < textLength; ++i) {
			int charW = font.getCharWidth(text[i]);
			if ((width + charW) > endX)
				break;

			boolean selected = i >= selectionStart & i < selectionEnd;
			if (selected)
				drawModalRect(startX + width, startX + width + charW, startY, endY, selectedLineColor);
			font.drawString(String.valueOf(text[i]), startX + width, startY, selected ? selectedTextColor : textColor);

			width += charW;
		}
	}

}
