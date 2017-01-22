package cofh.lib.util.numbers;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class ConstantProvider implements INumberProvider {

	protected Number min;

	public ConstantProvider(Number value) {

		if (value == null)
			throw new IllegalArgumentException("Null value not allowed");
		this.min = value;
	}

	public long longValue(World world, Random rand, BlockPos pos) {

		return min.longValue();
	}

	public double doubleValue(World world, Random rand, BlockPos pos) {

		return min.doubleValue();
	}
}
