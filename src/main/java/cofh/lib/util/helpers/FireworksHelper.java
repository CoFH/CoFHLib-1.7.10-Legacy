package cofh.lib.util.helpers;

import java.awt.Color;
import java.util.ArrayList;

import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/**
 * Contains helper functions to assist with creating {@link EntityFireworkRocket} instances.
 *
 * @author Tonius
 *
 */
public class FireworksHelper {

	public static enum FireworkType {
		BALL, LARGE_BALL, STAR, CREEPER, BURST;
	}

	/**
	 * Represents a single firework rocket and its properties.
	 * Can be used for manual fireworks creation.
	 */
	public static class Firework {

		private int flightDuration = 0;
		private boolean flicker = false;
		private boolean trail = false;
		private ArrayList<Integer> colors = new ArrayList<Integer>();
		private FireworkType type = FireworkType.BALL;

		/**
		 * Sets how long the fireworks will fly upwards before exploding.
		 *
		 * @param duration Duration before exploding in seconds (0 - 3)
		 * @return The current Firework instance
		 */
		public Firework setFlightDuration(int duration) {

			this.flightDuration = MathHelper.clampI(duration, 0, 3);
			return this;
		}

		/**
		 * Sets whether the fireworks should have the 'flicker' effect when exploding.
		 *
		 * @param flicker Whether to have the 'flicker' effect
		 * @return The current Firework instance
		 */
		public Firework setFlicker(boolean flicker) {

			this.flicker = flicker;
			return this;
		}

		/**
		 * Sets whether the fireworks should have the 'trail' effect when exploding.
		 *
		 * @param trail Whether to have the 'trail' effect
		 * @return The current Firework instance
		 */
		public Firework setTrail(boolean trail) {

			this.trail = trail;
			return this;
		}

		/**
		 * Sets the explosion type of the fireworks.
		 *
		 * @param type The explosion type
		 * @return The current Firework instance
		 */
		public Firework setType(FireworkType type) {

			this.type = type;
			return this;
		}

		/**
		 * Sets the explosion type of the fireworks.
		 *
		 * @param type The explosion type as an int
		 * @return The current Firework instance
		 */
		public Firework setType(int type) {

			this.setType(FireworkType.values()[MathHelper.clampI(type, 0,
					FireworkType.values().length - 1)]);
			return this;
		}

		/**
		 * Adds an RGB color to the explosion of the fireworks.
		 *
		 * @param red The RGB red value of the color to add (0 - 255)
		 * @param green The RGB green value of the color to add (0 - 255)
		 * @param blue The RGB blue value of the color to add (0 - 255)
		 * @return The current Firework instance
		 */
		public Firework addColor(int red, int green, int blue) {

			this.colors.add((red << 16) + (green << 8) + blue);
			return this;
		}

		/**
		 * @return The current Firework instance converted to an {@link ItemStack}
		 */
		public ItemStack getStack() {

			NBTTagCompound explosionTag = new NBTTagCompound();

			explosionTag.setBoolean("Flicker", this.flicker);
			explosionTag.setBoolean("Trail", this.trail);

			explosionTag.setByte("Type", (byte) this.type.ordinal());

			int[] colorArray = new int[this.colors.size()];
			for (int i = 0; i < this.colors.size(); i++) {
				colorArray[i] = this.colors.get(i);
			}
			explosionTag.setIntArray("Colors", colorArray);

			NBTTagCompound tags = new NBTTagCompound();

			NBTTagCompound fireworksTag = new NBTTagCompound();
			NBTTagList explosionsList = new NBTTagList();
			explosionsList.appendTag(explosionTag);

			fireworksTag.setByte("Flight", (byte) this.flightDuration);
			fireworksTag.setTag("Explosions", explosionsList);
			tags.setTag("Fireworks", fireworksTag);

			ItemStack stack = new ItemStack(Items.fireworks);
			stack.setTagCompound(tags);
			return stack;
		}

	}

	/**
	 * Generates a Firework instance with a random explosion type,
	 * a chance to have the 'flicker' and/or 'trail' effects, and up to 3 random colors.
	 *
	 * @return A Firework instance with randomized values. Can still be manipulated further
	 * to set things like the flight duration.
	 */
	public static Firework getRandomFirework() {

		Firework firework = new Firework();

		int v;
		switch (v = MathHelper.RANDOM.nextInt(4)) {
		case 2:
		case 0:
			firework.setFlicker(true);
			if (v == 0)
				break;
		case 1:
			firework.setTrail(true);
		}

		firework.setType(MathHelper.RANDOM.nextInt(5));

		for (int i = 0; i <= MathHelper.RANDOM.nextInt(3); i++) {
			Color randomColor = new Color(Color.HSBtoRGB(
					MathHelper.RANDOM.nextFloat() * 360,
					MathHelper.RANDOM.nextFloat() * 0.15F + 0.8F, 0.85F));
			firework.addColor(randomColor.getRed(), randomColor.getGreen(),
					randomColor.getBlue());
		}

		return firework;
	}

}
