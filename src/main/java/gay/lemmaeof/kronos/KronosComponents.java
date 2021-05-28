package gay.lemmaeof.kronos;

import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;

public class KronosComponents implements WorldComponentInitializer {
	@Override
	public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
		registry.register(Kronos.KRONOS_COMPONENT, KronosComponent::new);
	}
}
