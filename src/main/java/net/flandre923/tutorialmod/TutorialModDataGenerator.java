package net.flandre923.tutorialmod;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.flandre923.tutorialmod.datagen.*;

public class TutorialModDataGenerator implements DataGeneratorEntrypoint  {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(ModModelGenerator::new);
		pack.addProvider(ModLanguageGenerator::new);
		pack.addProvider(ModRecipeGenerator::new);
		pack.addProvider(ModLootTableGeneration::new);
		pack.addProvider(ModBlockTagGeneration::new);
	}
}
