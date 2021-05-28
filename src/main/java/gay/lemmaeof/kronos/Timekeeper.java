package gay.lemmaeof.kronos;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface Timekeeper {
	int getDuration(World world, BlockPos pos, BlockState state);
//	int getDuration(PlayerEntity player, ItemStack stack); //TODO
	default void activate(World world, BlockPos pos, BlockState state) {}
}
