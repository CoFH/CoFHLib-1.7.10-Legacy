package cofh.lib.world.feature;

import cofh.api.world.IFeatureGenerator;

import gnu.trove.set.hash.THashSet;

import java.util.Random;
import java.util.Set;

import net.minecraft.world.World;

public abstract class FeatureBase implements IFeatureGenerator {

	public static enum GenRestriction {
		NONE, BLACKLIST, WHITELIST
	}

	public final String name;
	public final GenRestriction biomeRestriction;
	public final GenRestriction dimensionRestriction;
	public final boolean regen;
	protected final Set<String> biomes = new THashSet<String>();
	protected final Set<Integer> dimensions = new THashSet<Integer>();

	/**
	 * Shortcut to add a Feature with no biome or dimension restriction.
	 */
	public FeatureBase(String name, boolean regen) {

		this(name, GenRestriction.NONE, regen, GenRestriction.NONE);
	}

	/**
	 * Shortcut to add a Feature with a dimension restriction but no biome restriction.
	 */
	public FeatureBase(String name, boolean regen, GenRestriction dimRes) {

		this(name, GenRestriction.NONE, regen, dimRes);
	}

	/**
	 * Shortcut to add a Feature with a biome restriction but no dimension restriction.
	 */
	public FeatureBase(String name, GenRestriction biomeRes, boolean regen) {

		this(name, biomeRes, regen, GenRestriction.NONE);
	}

	public FeatureBase(String name, GenRestriction biomeRes, boolean regen, GenRestriction dimRes) {

		this.name = name;
		this.biomeRestriction = biomeRes;
		this.dimensionRestriction = dimRes;
		this.regen = regen;
	}

	public FeatureBase addBiome(String biomeName) {

		biomes.add(biomeName);
		return this;
	}

	public FeatureBase addDimension(int dimID) {

		dimensions.add(dimID);
		return this;
	}

	/* IFeatureGenerator */
	@Override
	public final String getFeatureName() {

		return name;
	}

	@Override
	public abstract boolean generateFeature(Random random, int chunkX, int chunkZ, World world, boolean newGen);

}
