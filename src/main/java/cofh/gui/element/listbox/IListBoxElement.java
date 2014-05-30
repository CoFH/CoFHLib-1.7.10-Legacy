package cofh.gui.element.listbox;

import cofh.gui.element.ElementListBox;

public interface IListBoxElement {

	public int getHeight();

	public Object getValue();

	public void draw(ElementListBox listBox, int x, int y, int backColor, int textColor);
}
