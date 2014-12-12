package cofh.lib.util.helpers;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class FireworksHelper {

	public static enum FireworkType {
		BALL, LARGE_BALL, STAR, CREEPER, BURST;
	}

	public static class Firework {

		private int flightDuration = 0;
		private boolean flicker = false;
		private boolean trail = false;
		private ArrayList<Integer> colors = new ArrayList<Integer>();
		private FireworkType type = FireworkType.BALL;

		public Firework setFlightDuration(int duration) {

			if (duration >= 0 && duration <= 3) {
				this.flightDuration = duration;
			}
			return this;
		}

		public Firework setFlicker() {

			this.flicker = true;
			return this;
		}

		public Firework setTrail() {

			this.trail = true;
			return this;
		}

		public Firework setType(FireworkType type) {

			this.type = type;
			return this;
		}

		public Firework setType(int type) {

			if (type >= 0 && type <= 4) {
				this.setType(FireworkType.values()[type]);
			}
			return this;
		}

		public Firework addColor(int red, int green, int blue) {

			this.colors.add((red << 16) + (green << 8) + blue);
			return this;
		}

		public ItemStack getStack() {

			NBTTagCompound tags = new NBTTagCompound();

			NBTTagCompound fireworksTag = new NBTTagCompound();
			NBTTagList explosionsList = new NBTTagList();
			explosionsList.appendTag(this.getNBT());

			fireworksTag.setByte("Flight", (byte) this.flightDuration);
			fireworksTag.setTag("Explosions", explosionsList);
			tags.setTag("Fireworks", fireworksTag);

			ItemStack stack = new ItemStack(Items.fireworks);
			stack.setTagCompound(tags);
			return stack;
		}

		private NBTTagCompound getNBT() {

			NBTTagCompound explosionTag = new NBTTagCompound();

			explosionTag.setBoolean("Flicker", this.flicker);
			explosionTag.setBoolean("Trail", this.trail);

			explosionTag.setByte("Type", (byte) this.type.ordinal());

			int[] intArray = new int[this.colors.size()];
			for (int i = 0; i < this.colors.size(); i++) {
				intArray[i] = this.colors.get(i);
			}
			explosionTag.setIntArray("Colors", intArray);

			return explosionTag;
		}

	}

	public static ItemStack getRandomFirework() {

		Random rand = new Random();
		Firework firework = new Firework();

		int v;
		switch (v = rand.nextInt(4)) {
		case 2:
		case 0:
			firework.setFlicker();
			if (v == 0) break;
		case 1:
			firework.setTrail();
		}

		int type = rand.nextInt(5);
		firework.setType(type);

		for (int i = 0; i <= rand.nextInt(6); i++) {
			Color randomColor = new Color(Color.HSBtoRGB(
					rand.nextFloat() * 360, rand.nextFloat() * 0.15F + 0.8F,
					0.85F));
			firework.addColor(randomColor.getRed(), randomColor.getGreen(),
					randomColor.getBlue());
		}

		return firework.getStack();
	}

}
