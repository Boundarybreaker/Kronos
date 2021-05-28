package gay.lemmaeof.kronos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;

import net.minecraft.block.BlockState;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;

public class KronosComponent implements AutoSyncedComponent {
	private final World world;
	private int millis = 0;
	private final Map<BlockPos, Timekeeper> producers = new HashMap<>();
	private final Map<BlockPos, Timekeeper> consumers = new HashMap<>();

	public KronosComponent(World world) {
		this.world = world;
	}

	public int getMillis() {
		return millis;
	}

	public void setMillis(int millis) {
		this.millis = millis;
	}

	public void addMillis(int millis) {
		this.millis += millis;
	}

	//TODO: safer way to do this? this is just a hack for now lmao
	public void addProducer(BlockPos pos, Timekeeper keeper) {
		producers.put(pos, keeper);
	}

	public void removeProducer(BlockPos pos) {
		producers.remove(pos);
	}

	public void addConsumer(BlockPos pos, Timekeeper keeper) {
		consumers.put(pos, keeper);
	}

	public void removeConsumer(BlockPos pos) {
		consumers.remove(pos);
	}

	public void tick() {
		if (!world.isClient) {
			int lastMillis = millis;
			int produced = 0;
			int consumed = 0;
			millis = Math.max(0, millis - 50);
			for (BlockPos pos : producers.keySet()) {
				produced += producers.get(pos).getDuration(world, pos, world.getBlockState(pos));
			}
			for (BlockPos pos : consumers.keySet()) {
				consumed += consumers.get(pos).getDuration(world, pos, world.getBlockState(pos));
			}
			if (millis + produced >= consumed) {
				millis += produced;
				millis -= consumed;
				for (BlockPos pos : consumers.keySet()) {
					consumers.get(pos).activate(world, pos, world.getBlockState(pos));
				}
			}
			if (millis > 1000) {
				PlayerLookup.all(world.getServer()).forEach(player -> player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100)));
				PlayerLookup.all(world.getServer()).forEach(player -> player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 100)));
			}
			if (millis != lastMillis) Kronos.KRONOS_COMPONENT.sync(world);
		}
	}

	@Override
	public void readFromNbt(NbtCompound tag) {
		producers.clear();
		consumers.clear();
		millis = tag.getInt("Millis");
		long[] prodPos = tag.getLongArray("Producers");
		long[] consPos = tag.getLongArray("Consumers");
		for (long posL : prodPos) {
			BlockPos pos = BlockPos.fromLong(posL);
			BlockState state = world.getBlockState(pos);
			if (state.getBlock() instanceof Timekeeper) {
				producers.put(pos, (Timekeeper) state.getBlock());
			} else if (world.getBlockEntity(pos) instanceof Timekeeper) {
				producers.put(pos, (Timekeeper) world.getBlockEntity(pos));
			}
		}
		for (long posL : consPos) {
			BlockPos pos = BlockPos.fromLong(posL);
			BlockState state = world.getBlockState(pos);
			if (state.getBlock() instanceof Timekeeper) {
				consumers.put(pos, (Timekeeper) state.getBlock());
			} else if (world.getBlockEntity(pos) instanceof Timekeeper) {
				consumers.put(pos, (Timekeeper) world.getBlockEntity(pos));
			}
		}
	}

	@Override
	public void writeToNbt(NbtCompound tag) {
		List<Long> prodPos = new ArrayList<>();
		List<Long> consPos = new ArrayList<>();
		for (BlockPos pos : this.producers.keySet()) {
			prodPos.add(pos.asLong());
		}
		for (BlockPos pos : this.consumers.keySet()) {
			consPos.add(pos.asLong());
		}
		tag.putInt("Millis", millis);
		tag.putLongArray("Producers", prodPos);
		tag.putLongArray("Consumers", consPos);
	}
}
