package cofh.lib.gui.element;

import static org.lwjgl.opengl.GL11.*;

import cofh.lib.gui.GuiBase;
import cofh.lib.gui.GuiColor;
import cofh.lib.util.helpers.MathHelper;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;


public class ElementTextField extends ElementBase {

	public int borderColor       = new GuiColor( 55,  55,  55).getColor();
	public int backgroundColor   = new GuiColor(139, 139, 139).getColor();
	public int disabledColor     = new GuiColor(198, 198, 198).getColor();
	public int selectedLineColor = new GuiColor(160, 160, 224).getColor();
	public int textColor         = new GuiColor(224, 224, 224).getColor();
	public int selectedTextColor = new GuiColor(224, 224, 224).getColor();
	public int defaultCaretColor = new GuiColor(255, 255, 255).getColor();

	protected final char[] text;
	protected int textLength;
	protected int selectionStart, selectionEnd;
	protected int renderStart, caret;

	private boolean isFocused;
	private boolean canFocusChange = true;
	private boolean selecting;
	private byte caretCounter;
	protected boolean caretInsert;
	protected boolean enableStencil = true;

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

		if (canFocusChange) {
			isFocused = focused;
			caretCounter = 0;
		}
		return this;
	}

	public boolean isFocused() {

		return isEnabled() && isFocused;
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

	protected void clearSelection() {

		if (selectionStart != selectionEnd) {
			if (selectionEnd < textLength)
				System.arraycopy(text, selectionEnd, text, selectionStart, textLength - selectionEnd);
			textLength -= selectionEnd - selectionStart;

			selectionEnd = caret = selectionStart;
			findRenderStart();
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
			selectionEnd = caret = textLength;
			selectionStart = 0;
			findRenderStart();
			return true;
		case 3: // ^C
			if (selectionStart != selectionEnd) {
				GuiScreen.setClipboardString(getSelectedText());
			}
			return true;
		case 24: // ^X
			if (selectionStart != selectionEnd) {
				GuiScreen.setClipboardString(getSelectedText());
				clearSelection();
			}

			return true;
		case 22: // ^V
			// writeText(GuiScreen.getClipboardString());

			return true;
		default:
			switch (keyTyped) {
			case Keyboard.KEY_INSERT:
				if (GuiScreen.isShiftKeyDown()) {
					// writeText(GuiScreen.getClipboardString());
				} else {
					caretInsert = !caretInsert;
				}

				return true;
			case Keyboard.KEY_CLEAR: // mac only (clear selection)
				clearSelection();

				return true;
			case Keyboard.KEY_DELETE: // delete
				if (!GuiScreen.isShiftKeyDown()) {
					if (selectionStart != selectionEnd)
						clearSelection();
					else if (GuiScreen.isCtrlKeyDown())
						;//deleteWords(1);
					else {
						if (caret < textLength && textLength > 0) {
							--textLength;
							System.arraycopy(text, caret + 1, text, caret, textLength - caret);
						}
					}
					if (caret < renderStart)
						renderStart = MathHelper.clampI(caret - 3, 0, textLength);
					findRenderStart();

					return true;
				}
				// continue.. (shift+delete = backspace)
			case Keyboard.KEY_BACK: // backspace
				if (selectionStart != selectionEnd)
					clearSelection();
				else if (GuiScreen.isCtrlKeyDown())
					;// deleteWords(-1);
				else {
					if (caret > 0 && textLength > 0) {
						--caret;
						System.arraycopy(text, caret + 1, text, caret, textLength - caret);
						--textLength;
					}
				}
				if (caret < renderStart)
					renderStart = MathHelper.clampI(caret - 3, 0, textLength);
				findRenderStart();

				return true;
			case Keyboard.KEY_HOME: // home
				if (GuiScreen.isShiftKeyDown()) {
					if (caret > selectionEnd)
						selectionEnd = selectionStart;
					selectionStart = 0;
				} else
					selectionStart = selectionEnd = 0;
				renderStart = caret = 0;

				return true;
			case Keyboard.KEY_END: // end
				if (GuiScreen.isShiftKeyDown()) {
					if (caret < selectionStart)
						selectionStart = selectionEnd;
					selectionEnd = textLength;
				} else
					selectionStart = selectionEnd = textLength;
				caret = textLength;
				findRenderStart();

				return true;
			case Keyboard.KEY_LEFT: // left arrow
			case Keyboard.KEY_RIGHT: // right arrow
				int size = keyTyped == 203 ? -1 : 1;
				if (GuiScreen.isCtrlKeyDown())
					size = seekNextCaretLocation(caret, keyTyped == 205) - caret;

				if (selectionStart == selectionEnd || !GuiScreen.isShiftKeyDown())
					selectionStart = selectionEnd = caret;

				{
					int t = caret;
					caret = MathHelper.clampI(caret + size, 0, textLength);
					size = caret - t;
				}
				findRenderStart();

				if (GuiScreen.isShiftKeyDown()) {
					if (caret == selectionStart + size)
						selectionStart = caret;
					else if (caret == selectionEnd + size)
						selectionEnd = caret;
					// this logic is 'broken' in that the selection doesn't wrap
					// such that a|bc|def becomes abc|def| but it will highlight
					// the rest of the word the caret is on   i.e., a|bc|def -> a|bcdef|
					// i don't know that it matters (home+end exhibit the former)

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
					if (selectionStart != selectionEnd)
						clearSelection();

					if ((caretInsert && caret == text.length) || textLength == text.length)
						return true;

					if (!caretInsert) {
						if (caret < textLength)
							System.arraycopy(text, caret, text, caret + 1, textLength - caret);
						++textLength;
					}
					text[caret++] = charTyped;
					findRenderStart();
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

		++caretCounter;
		//if (selecting) {
		//	FontRenderer font = getFontRenderer();
		//	int pos = mouseX - posX - 1;
		//	for (int i = renderStart, width = 0; i < textLength; ++i) {
		//	}
		//}
	}

	@Override
	public void onMouseReleased(int mouseX, int mouseY) {

		if (!selecting)
			setFocused(false);
		selecting = false;
	}

	@Override
	public void drawBackground(int mouseX, int mouseY, float gameTicks) {

		drawModalRect(posX - 1, posY - 1, posX + sizeX + 1, posY + sizeY + 1, borderColor);
		drawModalRect(posX, posY, posX + sizeX, posY + sizeY, isEnabled() ? backgroundColor : disabledColor);
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {

		if (enableStencil) {
			glEnable(GL_STENCIL_TEST);
			glClear(GL_STENCIL_BUFFER_BIT);
			drawStencil(posX + 1, posY + 1, posX + sizeX - 1, posY + sizeY - 1, 1);
		}

		FontRenderer font = getFontRenderer();
		char[] text = this.text;
		int startX = posX + 1, endX = sizeX - 1, startY = posY + 1, endY = startY + font.FONT_HEIGHT;
		for (int i = renderStart, width = 0; i <= textLength; ++i) {
			boolean end = i == textLength;
			int charW = 2;
			if (!end) {
				charW = font.getCharWidth(text[i]);
				if (!enableStencil && (width + charW) > endX)
					break;
			}

			boolean drawCaret = i == caret && (caretCounter %= 24) < 12 && isFocused();
			if (drawCaret) {
				int caretEnd = width + 2;
				if (caretInsert)
					caretEnd = width + charW;
				drawModalRect(startX + width, startY - 1, startX + caretEnd, endY,
					(0xFF000000 & defaultCaretColor) | ~(defaultCaretColor & 0xFFFFFF));
			}

			if (!end) {
				boolean selected = i >= selectionStart & i < selectionEnd;
				if (selected)
					drawModalRect(startX + width, startY, startX + width + charW, endY, selectedLineColor);
				font.drawString(String.valueOf(text[i]), startX + width, startY, selected ? selectedTextColor : textColor);
			}

			if (drawCaret) {
				int caretEnd = width + 2;
				if (caretInsert)
					caretEnd = width + charW;

				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ZERO);
				gui.drawSizedRect(startX + width, startY - 1, startX + caretEnd, endY, -1);
				GL11.glDisable(GL11.GL_BLEND);
			}

			width += charW;
			if (width > endX)
				break;
		}

		if (enableStencil)
			glDisable(GL_STENCIL_TEST);
	}

}
