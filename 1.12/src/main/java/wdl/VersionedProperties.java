/*
 * This file is part of World Downloader: A mod to make backups of your
 * multiplayer worlds.
 * http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/2520465
 *
 * Copyright (c) 2014 nairol, cubic72
 * Copyright (c) 2017-2018 Pokechu22, julialy
 *
 * This project is licensed under the MMPLv2.  The full text of the MMPL can be
 * found in LICENSE.md, or online at https://github.com/iopleke/MMPLv2/blob/master/LICENSE.md
 * For information about this the MMPLv2, see http://stopmodreposts.org/
 *
 * Do not redistribute (in modified or unmodified form) without prior permission.
 */
package wdl;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import wdl.handler.block.BeaconHandler;
import wdl.handler.block.BlockHandler;
import wdl.handler.block.BrewingStandHandler;
import wdl.handler.block.ChestHandler;
import wdl.handler.block.DispenserHandler;
import wdl.handler.block.DropperHandler;
import wdl.handler.block.FurnaceHandler;
import wdl.handler.block.HopperHandler;
import wdl.handler.block.ShulkerBoxHandler;
import wdl.handler.entity.EntityHandler;
import wdl.handler.entity.HopperMinecartHandler;
import wdl.handler.entity.HorseHandler;
import wdl.handler.entity.StorageMinecartHandler;
import wdl.handler.entity.VillagerHandler;

/**
 * Helper that determines version-specific information about things, such
 * as whether a world has skylight.
 */
public class VersionedProperties {
	/**
	 * Returns true if the given world has skylight data.
	 *
	 * @return a boolean
	 */
	public static boolean hasSkyLight(World world) {
		// 1.11+: use hasSkyLight
		return world.provider.hasSkyLight();
	}

	/**
	 * Returns the ID used for block entities with the given class in the given version
	 *
	 * @return The ID, or an empty string if the given TE is not registered.
	 */
	public static String getBlockEntityID(Class<? extends TileEntity> clazz) {
		// 1.11+: use new IDs, and getKey exists.
		ResourceLocation loc = TileEntity.getKey(clazz);
		return (loc != null) ? loc.toString() : "";
	}

	/**
	 * All supported {@link BlockHandler}s.  Each type will only be represented once.
	 */
	public static final ImmutableList<BlockHandler<?, ?>> BLOCK_HANDLERS = ImmutableList.of(
			new BeaconHandler(),
			new BrewingStandHandler(),
			new ChestHandler(),
			new DispenserHandler(),
			new DropperHandler(),
			new FurnaceHandler(),
			new HopperHandler(),
			new ShulkerBoxHandler()
	);

	/**
	 * All supported {@link EntityHandler}s.  There will be no ambiguities.
	 */
	public static final ImmutableList<EntityHandler<?, ?>> ENTITY_HANDLERS = ImmutableList.of(
			new HopperMinecartHandler(),
			new HorseHandler(),
			new StorageMinecartHandler(),
			new VillagerHandler()
	);

	/**
	 * Checks if the given block is a shulker box, and the block entity ID matches.
	 */
	public static boolean isImportableShulkerBox(String entityID, Block block) {
		return block instanceof BlockShulkerBox && entityID.equals(getBlockEntityID(TileEntityShulkerBox.class));
	}

	/**
	 * Gets the class used to store the list of chunks in ChunkProviderClient.
	 */
	@SuppressWarnings("rawtypes")
	public static Class<Long2ObjectMap> getChunkListClass() {
		return Long2ObjectMap.class;
	}

	/**
	 * Creates a plugin message packet.
	 * @param channel The channel to send on.
	 * @param bytes The payload.
	 * @return The new packet.
	 */
	public static CPacketCustomPayload makePluginMessagePacket(String channel, byte[] bytes) {
		return new CPacketCustomPayload(channel, new PacketBuffer(Unpooled.copiedBuffer(bytes)));
	}

	/**
	 * Checks if the given game rule is of the given type.
	 * @param rules The rule collection
	 * @param rule The name of the rule
	 * @return The type, or null if no info could be found.
	 */
	@Nullable
	public static GameRules.ValueType getRuleType(GameRules rules, String rule) {
		for (GameRules.ValueType type : GameRules.ValueType.values()) {
			if (type == GameRules.ValueType.ANY_VALUE) {
				// Ignore this as it always returns true
				continue;
			}
			if (rules.areSameType(rule, type)) {
				return type;
			}
		}
		return null;
	}

	/**
	 * Gets the value of a game rule.
	 * @param rules The rule collection
	 * @param rule The name of the rule
	 * @return The value, or null if no info could be found.
	 */
	@Nullable
	public static String getRuleValue(GameRules rules, String rule) { 
		return rules.hasRule(rule) ? rules.getString(rule) : null;
	}

	/**
	 * Gets a list of all game rules.
	 * @param rules The rules object.
	 * @return A list of all rule names.
	 */
	public static List<String> getGameRules(GameRules rules) {
		return ImmutableList.copyOf(rules.getRules());
	}
}
