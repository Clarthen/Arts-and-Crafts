package com.kekecreations.arts_and_crafts.common.block.grower;

import com.kekecreations.arts_and_crafts.core.registry.ACFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class CorkTreeGrower extends AbstractTreeGrower {
    @Override
    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource randomSource, boolean bl) {
        return ACFeatures.ConfiguredFeatures.CORK_TREE;
    }
}