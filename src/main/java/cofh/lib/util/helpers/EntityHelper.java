package cofh.lib.util.helpers;

import cpw.mods.fml.common.FMLCommonHandler;

import java.util.Iterator;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
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

		int quadrant = cofh.lib.util.helpers.MathHelper.floor(living.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

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

	public static void transferEntityToWorld(Entity entity, WorldServer oldWorld, WorldServer newWorld) {

		WorldProvider pOld = oldWorld.provider;
		WorldProvider pNew = newWorld.provider;
		double moveFactor = pOld.getMovementFactor() / pNew.getMovementFactor();
		double x = entity.posX * moveFactor;
		double z = entity.posZ * moveFactor;

		oldWorld.theProfiler.startSection("placing");
		x = MathHelper.clamp_double(x, -29999872, 29999872);
		z = MathHelper.clamp_double(z, -29999872, 29999872);

		if (entity.isEntityAlive()) {
			entity.setLocationAndAngles(x, entity.posY, z, entity.rotationYaw, entity.rotationPitch);
			newWorld.spawnEntityInWorld(entity);
			newWorld.updateEntityWithOptionalForce(entity, false);
		}

		oldWorld.theProfiler.endSection();

		entity.setWorld(newWorld);
	}

	public static void transferPlayerToDimension(EntityPlayerMP player, int dimension, ServerConfigurationManager manager) {

		int oldDim = player.dimension;
		WorldServer worldserver = manager.getServerInstance().worldServerForDimension(player.dimension);
		player.dimension = dimension;
		WorldServer worldserver1 = manager.getServerInstance().worldServerForDimension(player.dimension);
		player.playerNetServerHandler.sendPacket(new S07PacketRespawn(player.dimension, player.worldObj.difficultySetting, player.worldObj.getWorldInfo()
				.getTerrainType(), player.theItemInWorldManager.getGameType()));
		worldserver.removePlayerEntityDangerously(player);
		player.isDead = false;
		transferEntityToWorld(player, worldserver, worldserver1);
		manager.func_72375_a(player, worldserver);
		player.playerNetServerHandler.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
		player.theItemInWorldManager.setWorld(worldserver1);
		manager.updateTimeAndWeatherForPlayer(player, worldserver1);
		manager.syncPlayerInventory(player);
		Iterator<PotionEffect> iterator = player.getActivePotionEffects().iterator();

		while (iterator.hasNext()) {
			PotionEffect potioneffect = iterator.next();
			player.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(player.getEntityId(), potioneffect));
		}
		FMLCommonHandler.instance().firePlayerChangedDimensionEvent(player, oldDim, dimension);
	}

}
