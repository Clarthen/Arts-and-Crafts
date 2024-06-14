package com.kekecreations.arts_and_crafts.common.util;

import com.kekecreations.arts_and_crafts.common.block.DyedDecoratedPotBlock;
import com.kekecreations.arts_and_crafts.common.entity.DyedDecoratedPotBlockEntity;
import com.kekecreations.arts_and_crafts.common.item.palette.PaintbrushPalette;
import com.kekecreations.arts_and_crafts.common.misc.KekeBlockStateProperties;
import com.kekecreations.arts_and_crafts.core.mixin.DecoratedPotBlockEntityAccessor;
import com.kekecreations.arts_and_crafts.core.registry.ACRegistries;
import com.kekecreations.arts_and_crafts.core.registry.ACSounds;
import com.kekecreations.arts_and_crafts.core.registry.ACBlocks;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PaintbrushUtils {

    public static BlockState placePotStatesFromAnotherBlock(BlockState blockState, BlockState oldState) {
        return blockState
                .setValue(BlockStateProperties.HORIZONTAL_FACING, oldState.getValue(BlockStateProperties.HORIZONTAL_FACING))
                .setValue(BlockStateProperties.CRACKED, oldState.getValue(BlockStateProperties.CRACKED))
                .setValue(BlockStateProperties.WATERLOGGED, oldState.getValue(BlockStateProperties.WATERLOGGED));
    }

    public static void setPotDecorations(Level level, BlockPos pos, DecoratedPotBlockEntity.Decorations decorations) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof DyedDecoratedPotBlockEntity dyedPot) {
            dyedPot.setDecoration(decorations);
        } else if (blockEntity instanceof DecoratedPotBlockEntityAccessor decoratedPot) {
            decoratedPot.setDecorations(decorations);
        }
    }

    @Nullable
    public static Block getFinalBlock(RegistryAccess access, BlockState state, ItemStack stack) {
        Optional<PaintbrushPalette> optionalPalette = access.registryOrThrow(ACRegistries.PAINTBRUSH_PALETTE).stream().filter(
                searchPalette -> searchPalette.blocks().contains(state.getBlockHolder())
        ).findFirst();
        if (optionalPalette.isEmpty()) return null;

        PaintbrushPalette palette = optionalPalette.get();
        Holder<Block> holder = palette.mappings().get(stack.getItemHolder());

        if (holder.unwrapKey().isEmpty()) return null;
        
        return access.registryOrThrow(Registries.BLOCK).getOrThrow(holder.unwrapKey().get());
    }

    public static void damagePaintbrushWhenPainting(Level level, Player player, ItemStack itemStack, BlockState blockState, BlockPos pos, InteractionHand hand) {
        if (!player.getAbilities().instabuild) {
            if (player instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.PLACED_BLOCK.trigger(serverPlayer, pos, itemStack);
            }
            blockState.getBlock().setPlacedBy(level, pos, blockState, player, itemStack);
            itemStack.hurtAndBreak(1, player, (entity) -> entity.broadcastBreakEvent(hand));
            if (itemStack.isEmpty()) {
                ItemStack itemStack2 = new ItemStack(Items.BRUSH);
                itemStack2.setTag(itemStack.getTag());
                player.setItemInHand(hand, itemStack2);
            }

        }

    }
    public static void paintbrushItemEvents(Level level, BlockState state, BlockPos pos, Player player, ItemStack itemStack, InteractionHand hand) {
        level.playSound(null, pos, ACSounds.PAINT_WITH_PAINTBRUSH.get(), SoundSource.BLOCKS, 0.5F, 1.0F);
        level.gameEvent(GameEvent.BLOCK_PLACE, pos, GameEvent.Context.of(player, state));
        player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
        damagePaintbrushWhenPainting(level, player, itemStack, state, pos, hand);
    }

    public static void paintBlock(Level level, BlockState blockStateToPlace, BlockPos pos, Player player, ItemStack itemStack, InteractionHand hand) {
        BlockState blockState = level.getBlockState(pos);
        level.setBlockAndUpdate(pos, blockStateToPlace.getBlock().withPropertiesOf(blockState));
        paintbrushItemEvents(level, blockState, pos, player, itemStack,  hand);
    }

    public static void paintBed(Level level, BlockState blockStateToPlace, BlockPos pos, Player player, ItemStack itemStack, InteractionHand hand) {
        BlockState blockState = level.getBlockState(pos);
        if (blockState.getValue(BlockStateProperties.BED_PART) == BedPart.FOOT) {
            level.setBlockAndUpdate(pos.relative(blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)), Blocks.AIR.defaultBlockState());
            level.setBlockAndUpdate(pos, blockStateToPlace
                    .setValue(BlockStateProperties.HORIZONTAL_FACING, blockState.getValue(BlockStateProperties.HORIZONTAL_FACING))
                    .setValue(BlockStateProperties.BED_PART, BedPart.FOOT)
                    .setValue(BlockStateProperties.OCCUPIED, blockState.getValue(BlockStateProperties.OCCUPIED)));
            level.setBlockAndUpdate(pos.relative(blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)), blockStateToPlace
                    .setValue(BlockStateProperties.HORIZONTAL_FACING, blockState.getValue(BlockStateProperties.HORIZONTAL_FACING))
                    .setValue(BlockStateProperties.BED_PART, BedPart.HEAD)
                    .setValue(BlockStateProperties.OCCUPIED, blockState.getValue(BlockStateProperties.OCCUPIED)));
        } else {
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            level.setBlockAndUpdate(pos, blockStateToPlace
                    .setValue(BlockStateProperties.HORIZONTAL_FACING, blockState.getValue(BlockStateProperties.HORIZONTAL_FACING))
                    .setValue(BlockStateProperties.BED_PART, BedPart.HEAD)
                    .setValue(BlockStateProperties.OCCUPIED, blockState.getValue(BlockStateProperties.OCCUPIED)));
            level.setBlockAndUpdate(pos.relative(blockState.getValue(BlockStateProperties.HORIZONTAL_FACING), -1), blockStateToPlace
                    .setValue(BlockStateProperties.HORIZONTAL_FACING, blockState.getValue(BlockStateProperties.HORIZONTAL_FACING))
                    .setValue(BlockStateProperties.BED_PART, BedPart.FOOT)
                    .setValue(BlockStateProperties.OCCUPIED, blockState.getValue(BlockStateProperties.OCCUPIED)));
        }
        itemStack.hurtAndBreak(1, player, (entity) -> entity.broadcastBreakEvent(hand));
        level.playSound(null, pos, SoundEvents.GLOW_INK_SAC_USE, SoundSource.BLOCKS, 1.0f, 1.0f);
        level.gameEvent(GameEvent.BLOCK_PLACE, pos, GameEvent.Context.of(player, blockState));
    }

    public static void paintPlaster(Level level, BlockState blockStateToPlace, BlockPos pos, Player player, ItemStack itemStack, InteractionHand hand) {
        BlockState blockState = level.getBlockState(pos);
        level.setBlockAndUpdate(pos, blockStateToPlace
                .setValue(BlockStateProperties.WATERLOGGED, blockState.getValue(BlockStateProperties.WATERLOGGED))
                .setValue(BlockStateProperties.FACING, blockState.getValue(BlockStateProperties.FACING)));
        paintbrushItemEvents(level, blockState, pos, player, itemStack,  hand);
    }

    public static void paintChalkDust(Level level, BlockState blockStateToPlace, BlockPos pos, Player player, ItemStack itemStack, InteractionHand hand) {
        BlockState blockState = level.getBlockState(pos);
        level.setBlockAndUpdate(pos, blockStateToPlace
                .setValue(KekeBlockStateProperties.CHALK_PATTERN, blockState.getValue(KekeBlockStateProperties.CHALK_PATTERN))
                .setValue(BlockStateProperties.FACING, blockState.getValue(BlockStateProperties.FACING)));
        paintbrushItemEvents(level, blockState, pos, player, itemStack,  hand);
    }
    public static void paintShulkerBox(Level level, BlockState blockStateToPlace, BlockPos pos, Player player, ItemStack itemStack, InteractionHand hand) {
        BlockState blockState = level.getBlockState(pos);
        level.setBlockAndUpdate(pos, blockStateToPlace
                .setValue(BlockStateProperties.FACING, blockState.getValue(BlockStateProperties.FACING)));
        paintbrushItemEvents(level, blockState, pos, player, itemStack,  hand);
    }

    public static void paintGlassPane(Level level, BlockState blockStateToPlace, BlockPos pos, Player player, ItemStack itemStack, InteractionHand hand) {
        BlockState blockState = level.getBlockState(pos);
        level.setBlockAndUpdate(pos, blockStateToPlace
                .setValue(BlockStateProperties.NORTH, blockState.getValue(BlockStateProperties.NORTH))
                .setValue(BlockStateProperties.EAST, blockState.getValue(BlockStateProperties.EAST))
                .setValue(BlockStateProperties.SOUTH, blockState.getValue(BlockStateProperties.SOUTH))
                .setValue(BlockStateProperties.WEST, blockState.getValue(BlockStateProperties.WEST))
                .setValue(BlockStateProperties.WATERLOGGED, blockState.getValue(BlockStateProperties.WATERLOGGED)));
        paintbrushItemEvents(level, blockState, pos, player, itemStack,  hand);
    }

    public static void paintCandles(Level level, BlockState blockStateToPlace, BlockPos pos, Player player, ItemStack itemStack, InteractionHand hand) {
        BlockState blockState = level.getBlockState(pos);
        level.setBlockAndUpdate(pos, blockStateToPlace
                .setValue(BlockStateProperties.WATERLOGGED, blockState.getValue(BlockStateProperties.WATERLOGGED))
                .setValue(BlockStateProperties.LIT, blockState.getValue(BlockStateProperties.LIT))
                .setValue(BlockStateProperties.CANDLES, blockState.getValue(BlockStateProperties.CANDLES)));
        paintbrushItemEvents(level, blockState, pos, player, itemStack,  hand);
    }
    public static void paintSlab(Level level, BlockState blockStateToPlace, BlockPos pos, Player player, ItemStack itemStack, InteractionHand hand) {
        BlockState blockState = level.getBlockState(pos);
        level.setBlockAndUpdate(pos, blockStateToPlace
                .setValue(BlockStateProperties.SLAB_TYPE, blockState.getValue(BlockStateProperties.SLAB_TYPE))
                .setValue(BlockStateProperties.WATERLOGGED, blockState.getValue(BlockStateProperties.WATERLOGGED)));
        paintbrushItemEvents(level, blockState, pos, player, itemStack,  hand);
    }
    public static void paintStairs(Level level, BlockState blockStateToPlace, BlockPos pos, Player player, ItemStack itemStack, InteractionHand hand) {
        BlockState blockState = level.getBlockState(pos);
        level.setBlockAndUpdate(pos, blockStateToPlace
                .setValue(BlockStateProperties.WATERLOGGED, blockState.getValue(BlockStateProperties.WATERLOGGED))
                .setValue(BlockStateProperties.HALF, blockState.getValue(BlockStateProperties.HALF))
                .setValue(BlockStateProperties.STAIRS_SHAPE, blockState.getValue(BlockStateProperties.STAIRS_SHAPE))
                .setValue(BlockStateProperties.HORIZONTAL_FACING, blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)));
        paintbrushItemEvents(level, blockState, pos, player, itemStack,  hand);
    }
    public static void paintWall(Level level, BlockState blockStateToPlace, BlockPos pos, Player player, ItemStack itemStack, InteractionHand hand) {
        BlockState blockState = level.getBlockState(pos);
        level.setBlockAndUpdate(pos, blockStateToPlace
                .setValue(BlockStateProperties.WATERLOGGED, blockState.getValue(BlockStateProperties.WATERLOGGED))
                .setValue(BlockStateProperties.UP, blockState.getValue(BlockStateProperties.UP))
                .setValue(BlockStateProperties.NORTH_WALL, blockState.getValue(BlockStateProperties.NORTH_WALL))
                .setValue(BlockStateProperties.EAST_WALL, blockState.getValue(BlockStateProperties.EAST_WALL))
                .setValue(BlockStateProperties.SOUTH_WALL, blockState.getValue(BlockStateProperties.SOUTH_WALL))
                .setValue(BlockStateProperties.WEST_WALL, blockState.getValue(BlockStateProperties.WEST_WALL)));
        paintbrushItemEvents(level, blockState, pos, player, itemStack,  hand);
    }


    public static void paintDecoratedPot(Level level, BlockEntity blockEntity, BlockPos pos, Player player, ItemStack itemStack, InteractionHand hand, DyeColor paintbrushDyeColour) {
        BlockState blockState = level.getBlockState(pos);
        if (blockEntity instanceof DyedDecoratedPotBlockEntity dyedDecoratedPotBlockEntity) {
            DecoratedPotBlockEntity.Decorations oldDecorations = dyedDecoratedPotBlockEntity.getDecorations();
            DyedDecoratedPotBlock placedPot = (DyedDecoratedPotBlock) ACBlocks.getDyedDecoratedPot(paintbrushDyeColour.getId());
            level.setBlockAndUpdate(pos, PaintbrushUtils.placePotStatesFromAnotherBlock(placedPot.defaultBlockState(), blockState));
            PaintbrushUtils.setPotDecorations(level, pos, oldDecorations);
        }
        else if (blockEntity instanceof DecoratedPotBlockEntity decoratedPotBlockEntity) {
            DecoratedPotBlockEntity.Decorations oldDecorations = decoratedPotBlockEntity.getDecorations();
            DyedDecoratedPotBlock placedPot = (DyedDecoratedPotBlock) ACBlocks.getDyedDecoratedPot(paintbrushDyeColour.getId());
            level.setBlockAndUpdate(pos, PaintbrushUtils.placePotStatesFromAnotherBlock(placedPot.defaultBlockState(), blockState));
            PaintbrushUtils.setPotDecorations(level, pos, oldDecorations);
        }
        paintbrushItemEvents(level, blockState, pos, player, itemStack,  hand);
    }
}
