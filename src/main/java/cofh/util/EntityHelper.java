package cofh.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * This class contains various helper functions related to Entities.
 * 
 * @author King Lemming
 * 
 */
public class EntityHelper {

	private EntityHelper() {

	}

	public static int getEntityFacingCardinal(EntityLivingBase living) {

		int quadrant = MathHelper.floor(living.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

		switch (quadrant) {
		case 0:
			return 2;
		case 1:
			return 5;
		case 2:
			return 3;
		default:
			return 4;
		}
	}

	public static ForgeDirection getEntityFacingForgeDirection(EntityLivingBase living) {

		return ForgeDirection.VALID_DIRECTIONS[getEntityFacingCardinal(living)];
	}

}
