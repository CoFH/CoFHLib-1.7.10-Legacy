package cofh.lib.util.numbers;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public interface INumberProvider {

	default int intValue(World world, Random rand, BlockPos pos) {

		return (int) longValue(world, rand, pos);
	}

	long longValue(World world, Random rand, BlockPos pos);

	default float floatValue(World world, Random rand, BlockPos pos) {

		return (float) doubleValue(world, rand, pos);
	}

	double doubleValue(World world, Random rand, BlockPos pos);

}
