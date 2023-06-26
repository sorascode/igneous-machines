
package com.herdlicka.igneousmachines.mixin;

import com.herdlicka.igneousmachines.IgneousCrafterBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(IgneousCrafterBlockEntity.class)
public abstract class IgneousCrafterBlockEntityMixin {
	@Unique
	private static final ThreadLocal<ItemStack> REMAINDER_STACK = new ThreadLocal<>();

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"), locals = LocalCapture.CAPTURE_FAILHARD, allow = 1)
	private static void getStackRemainder(World world, BlockPos pos, BlockState state, IgneousCrafterBlockEntity blockEntity, CallbackInfo ci) {
		REMAINDER_STACK.set(blockEntity.getStack(9).getRecipeRemainder());
	}

	@ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/DefaultedList;set(ILjava/lang/Object;)Ljava/lang/Object;"), index = 1, allow = 1)
	private static <E> E setStackRemainder(E element) {
		E remainder = (E) REMAINDER_STACK.get();
		REMAINDER_STACK.remove();
		return remainder;
	}
}
