package mcp.mobius.waila.impl;

import java.util.EnumMap;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.EntityAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IWailaClientRegistration;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.config.WailaConfig;
import mcp.mobius.waila.api.ui.IDisplayHelper;
import mcp.mobius.waila.api.ui.IElementHelper;
import mcp.mobius.waila.impl.ui.ElementHelper;
import mcp.mobius.waila.overlay.DisplayHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class WailaClientRegistration implements IWailaClientRegistration {

	public static final WailaClientRegistration INSTANCE = new WailaClientRegistration();

	public final HierarchyLookup<IComponentProvider> blockIconProviders;
	public final EnumMap<TooltipPosition, HierarchyLookup<IComponentProvider>> blockComponentProviders;

	public final HierarchyLookup<IEntityComponentProvider> entityIconProviders;
	public final EnumMap<TooltipPosition, HierarchyLookup<IEntityComponentProvider>> entityComponentProviders;

	public final Set<Block> hideBlocks = Sets.newHashSet();
	public final Set<EntityType<?>> hideEntities = Sets.newHashSet();
	public final Set<Block> pickBlocks = Sets.newHashSet();

	WailaClientRegistration() {
		blockIconProviders = new HierarchyLookup<>(Block.class);
		blockComponentProviders = new EnumMap<>(TooltipPosition.class);

		entityIconProviders = new HierarchyLookup<>(Entity.class);
		entityComponentProviders = new EnumMap<>(TooltipPosition.class);

		for (TooltipPosition position : TooltipPosition.values()) {
			blockComponentProviders.put(position, new HierarchyLookup<>(Block.class));
			entityComponentProviders.put(position, new HierarchyLookup<>(Entity.class));
		}
	}

	@Override
	public void registerIconProvider(IComponentProvider dataProvider, Class<? extends Block> block) {
		blockIconProviders.register(block, dataProvider);
	}

	@Override
	public void registerComponentProvider(IComponentProvider dataProvider, TooltipPosition position, Class<? extends Block> block) {
		blockComponentProviders.get(position).register(block, dataProvider);
	}

	@Override
	public void registerIconProvider(IEntityComponentProvider dataProvider, Class<? extends Entity> entity) {
		entityIconProviders.register(entity, dataProvider);
	}

	@Override
	public void registerComponentProvider(IEntityComponentProvider dataProvider, TooltipPosition position, Class<? extends Entity> entity) {
		entityComponentProviders.get(position).register(entity, dataProvider);
	}

	public List<IComponentProvider> getBlockProviders(Block block, TooltipPosition position) {
		return blockComponentProviders.get(position).get(block);
	}

	public List<IComponentProvider> getBlockIconProviders(Block block) {
		return blockIconProviders.get(block);
	}

	public List<IEntityComponentProvider> getEntityProviders(Entity entity, TooltipPosition position) {
		return entityComponentProviders.get(position).get(entity);
	}

	public List<IEntityComponentProvider> getEntityIconProviders(Entity entity) {
		return entityIconProviders.get(entity);
	}

	@Override
	public void hideTarget(Block block) {
		hideBlocks.add(block);
	}

	@Override
	public void hideTarget(EntityType<?> entityType) {
		hideEntities.add(entityType);
	}

	@Override
	public void usePickedResult(Block block) {
		pickBlocks.add(block);
	}

	@Override
	public boolean shouldHide(BlockState state) {
		return hideBlocks.contains(state.getBlock());
	}

	@Override
	public boolean shouldPick(BlockState state) {
		return pickBlocks.contains(state.getBlock());
	}

	@Override
	public boolean shouldHide(Entity entity) {
		return hideEntities.contains(entity.getType());
	}

	@Override
	public BlockAccessor createBlockAccessor(BlockState blockState, BlockEntity blockEntity, Level level, Player player, CompoundTag serverData, BlockHitResult hit, boolean serverConnected) {
		return new BlockAccessorImpl(blockState, blockEntity, level, player, serverData, hit, serverConnected);
	}

	@Override
	public EntityAccessor createEntityAccessor(Entity entity, Level level, Player player, CompoundTag serverData, EntityHitResult hit, boolean serverConnected) {
		return new EntityAccessorImpl(entity, level, player, serverData, hit, serverConnected);
	}

	@Override
	public IElementHelper getElementHelper() {
		return ElementHelper.INSTANCE;
	}

	@Override
	public IDisplayHelper getDisplayHelper() {
		return DisplayHelper.INSTANCE;
	}

	@Override
	public WailaConfig getConfig() {
		return Waila.CONFIG.get();
	}

}
