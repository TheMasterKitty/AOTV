package themasterkitty.aotv.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import themasterkitty.aotv.AOTV;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
	@Unique
	private boolean run = false;
	@Shadow @Nullable public ClientPlayerEntity player;

	@Shadow @Nullable public ClientWorld world;

	@Inject(at = @At("HEAD"), method = "doItemUse", cancellable = true)
	public void doItemUse(CallbackInfo ci) {
		if (AOTV.dist != 0 && player != null && player.getMainHandStack().getItem() == Items.DIAMOND_SHOVEL && !player.isSneaking()) {
			run = !run;
			if (!run) return;

			Vec3d lookDirection = player.getRotationVec(1.0F);
			Vec3d playerPos = player.getPos();
			Vec3d valid = player.getPos();

			assert world != null;
			for (double i = 0.5; i <= AOTV.dist; i += 0.5) {
				Vec3d pos = playerPos.add(lookDirection.multiply(i));
				BlockPos blockPos = new BlockPos((int) pos.x, (int) pos.y, (int) pos.z);

                if (!world.getBlockState(blockPos).isSolid()) {
					valid = pos;
				}
			}
			if (valid != player.getPos())
				player.setPosition(valid);
			ci.cancel();
		}
	}
}