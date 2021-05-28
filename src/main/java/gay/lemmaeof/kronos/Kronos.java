package gay.lemmaeof.kronos;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import gay.lemmaeof.kronos.block.DemoTimekeeperBlock;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

public class Kronos implements ModInitializer {
	public static final String MODID = "kronos";
	public static final ComponentKey<KronosComponent> KRONOS_COMPONENT =
		ComponentRegistry.getOrCreate(new Identifier(MODID, MODID), KronosComponent.class);

	@Override
	public void onInitialize() {
		ServerTickEvents.START_WORLD_TICK.register(world -> KRONOS_COMPONENT.get(world).tick());
		registerDemo(1, true);
		registerDemo(5, true);
		registerDemo(10, true);
		registerDemo(1, false);
		registerDemo(5, false);
		registerDemo(10, false);
	}

	private static Block registerDemo(int amount, boolean produces) {
		Identifier id = new Identifier(MODID, (produces? "producer" : "consumer") + "_" + amount);
		Block block = Registry.register(Registry.BLOCK, id, new DemoTimekeeperBlock(amount, produces, FabricBlockSettings.of(Material.STONE)));
		Registry.register(Registry.ITEM, id, new BlockItem(block, new Item.Settings().group(ItemGroup.MISC)));
		return block;
	}
}
