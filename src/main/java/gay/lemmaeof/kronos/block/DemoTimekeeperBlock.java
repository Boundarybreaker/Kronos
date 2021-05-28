package gay.lemmaeof.kronos.block;

import gay.lemmaeof.kronos.Kronos;
import gay.lemmaeof.kronos.Timekeeper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DemoTimekeeperBlock extends Block implements Timekeeper {
	private final int duration;
	private final boolean produces;

	public DemoTimekeeperBlock(int duration, boolean produces, Settings settings) {
		super(settings);
		this.duration = duration;
		this.produces = produces;
	}

	@Override
	public int getDuration(World world, BlockPos pos, BlockState state) {
		return duration;
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		super.onBlockAdded(state, world, pos, oldState, notify);
		if (produces) {
			Kronos.KRONOS_COMPONENT.get(world).addProducer(pos, this);
		} else {
			Kronos.KRONOS_COMPONENT.get(world).addConsumer(pos, this);
		}
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		super.onStateReplaced(state, world, pos, newState, moved);
		if (state.getBlock() != newState.getBlock()) {
			if (produces) {
				Kronos.KRONOS_COMPONENT.get(world).removeProducer(pos);
			} else {
				Kronos.KRONOS_COMPONENT.get(world).removeConsumer(pos);
			}
		}
	}
}
