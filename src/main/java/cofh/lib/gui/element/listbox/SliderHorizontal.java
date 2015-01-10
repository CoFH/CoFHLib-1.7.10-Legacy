package cofh.lib.gui.element.listbox;

import cofh.lib.gui.GuiBase;
import cofh.lib.gui.element.ElementSlider;

public abstract class SliderHorizontal extends ElementSlider {

	public SliderHorizontal(GuiBase containerScreen, int x, int y, int width, int height, int maxValue) {

		this(containerScreen, x, y, width, height, maxValue, 0);
	}

	public SliderHorizontal(GuiBase containerScreen, int x, int y, int width, int height, int maxValue, int minValue) {

		super(containerScreen, x, y, width, height, maxValue, minValue);
		int dist = maxValue - minValue;
		setSliderSize(dist <= 0 ? width : Math.max(width / dist, 9), height);
	}

	@Override
	public int getSliderX() {

		int dist = _valueMax - _valueMin;
		return Math.min(dist == 0 ? 0 : (sizeX - _sliderWidth) * (_value - _valueMin) / dist, sizeX - _sliderWidth);
	}

	@Override
	public void dragSlider(int v, int y) {

		v += Math.round(_sliderWidth * (v / (float) sizeX - 0.5f));
		setValue(_valueMin + ((_valueMax - _valueMin) * v / sizeX));
	}
}
