package gay.lemmaeof.kronos.client;

import gay.lemmaeof.kronos.Kronos;
import gay.lemmaeof.kronos.KronosComponent;

import net.minecraft.client.MinecraftClient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

@Environment(net.fabricmc.api.EnvType.CLIENT)
public class KronosClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		HudRenderCallback.EVENT.register(((matrices, tickDelta) -> {
			MinecraftClient mc = MinecraftClient.getInstance();
			if (mc.world != null) {
				KronosComponent comp = Kronos.KRONOS_COMPONENT.get(mc.world);
				mc.textRenderer.draw(matrices, comp.getMillis() + " ms/t", 16F, 16F, 0xFFFFFF);
			}
		}));
	}
}
