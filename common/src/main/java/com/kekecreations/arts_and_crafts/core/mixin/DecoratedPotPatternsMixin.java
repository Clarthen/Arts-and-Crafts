package com.kekecreations.arts_and_crafts.core.mixin;

import com.kekecreations.arts_and_crafts.core.registry.ACCustomSherdPatterns;
import com.kekecreations.arts_and_crafts.core.registry.ACItems;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.DecoratedPotPatterns;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DecoratedPotPatterns.class)
public abstract class DecoratedPotPatternsMixin {


    @Inject(method = "getResourceKey", at = @At("RETURN"), cancellable = true)
    private static void arts_and_crafts_addedPatterns(Item item, CallbackInfoReturnable<ResourceKey<String>> cir) {

        if (item == ACItems.ROLL_POTTERY_SHERD.get()) {
            cir.setReturnValue(ACCustomSherdPatterns.ROLL_POTTERY_PATTERN);
        }
        if (item == ACItems.RUINED_POTTERY_SHERD.get()) {
            cir.setReturnValue(ACCustomSherdPatterns.RUINED_POTTERY_PATTERN);
        }
        if (item == ACItems.FINALE_POTTERY_SHERD.get()) {
            cir.setReturnValue(ACCustomSherdPatterns.FINALE_POTTERY_PATTERN);
        }
        if (item == ACItems.GATEWAY_POTTERY_SHERD.get()) {
            cir.setReturnValue(ACCustomSherdPatterns.GATEWAY_POTTERY_PATTERN);
        }
        if (item == ACItems.POTTERY_SHERD.get()) {
            cir.setReturnValue(ACCustomSherdPatterns.POTTERY_PATTERN);
        }
    }

    @Inject(method = "bootstrap", at = @At("TAIL"))
    private static void arts_and_crafts_bootstrap(Registry<String> registry, CallbackInfoReturnable<String> cir) {
        //NORMAL
        Registry.register(registry, ACCustomSherdPatterns.POTTERY_PATTERN, "pottery_pattern");
        Registry.register(registry, ACCustomSherdPatterns.ROLL_POTTERY_PATTERN, "roll_pottery_pattern");
        Registry.register(registry, ACCustomSherdPatterns.RUINED_POTTERY_PATTERN, "ruined_pottery_pattern");
        Registry.register(registry, ACCustomSherdPatterns.FINALE_POTTERY_PATTERN, "finale_pottery_pattern");
        Registry.register(registry, ACCustomSherdPatterns.GATEWAY_POTTERY_PATTERN, "gateway_pottery_pattern");
        //BLACK
        Registry.register(registry, ACCustomSherdPatterns.BLACK_DECORATED_POT_BASE, "black_decorated_pot_base");
        //BLUE
        Registry.register(registry, ACCustomSherdPatterns.BLUE_DECORATED_POT_BASE, "blue_decorated_pot_base");
        //BROWN
        Registry.register(registry, ACCustomSherdPatterns.BROWN_DECORATED_POT_BASE, "brown_decorated_pot_base");
        //CYAN
        Registry.register(registry, ACCustomSherdPatterns.CYAN_DECORATED_POT_BASE, "cyan_decorated_pot_base");
        //GRAY
        Registry.register(registry, ACCustomSherdPatterns.GRAY_DECORATED_POT_BASE, "gray_decorated_pot_base");
        //GREEN
        Registry.register(registry, ACCustomSherdPatterns.GREEN_DECORATED_POT_BASE, "green_decorated_pot_base");
        //LIGHT_BLUE
        Registry.register(registry, ACCustomSherdPatterns.LIGHT_BLUE_DECORATED_POT_BASE, "light_blue_decorated_pot_base");
        //LIGHT_GRAY
        Registry.register(registry, ACCustomSherdPatterns.LIGHT_GRAY_DECORATED_POT_BASE, "light_gray_decorated_pot_base");
        //LIME
        Registry.register(registry, ACCustomSherdPatterns.LIME_DECORATED_POT_BASE, "lime_decorated_pot_base");
        //MAGENTA
        Registry.register(registry, ACCustomSherdPatterns.MAGENTA_DECORATED_POT_BASE, "magenta_decorated_pot_base");
        //ORANGE
        Registry.register(registry, ACCustomSherdPatterns.ORANGE_DECORATED_POT_BASE, "orange_decorated_pot_base");
        //PINK
        Registry.register(registry, ACCustomSherdPatterns.PINK_DECORATED_POT_BASE, "pink_decorated_pot_base");
        //PURPLE
        Registry.register(registry, ACCustomSherdPatterns.PURPLE_DECORATED_POT_BASE, "purple_decorated_pot_base");
        //RED
        Registry.register(registry, ACCustomSherdPatterns.RED_DECORATED_POT_BASE, "red_decorated_pot_base");
        //WHITE
        Registry.register(registry, ACCustomSherdPatterns.WHITE_DECORATED_POT_BASE, "white_decorated_pot_base");
        //YELLOW
        Registry.register(registry, ACCustomSherdPatterns.YELLOW_DECORATED_POT_BASE, "yellow_decorated_pot_base");
    }
}