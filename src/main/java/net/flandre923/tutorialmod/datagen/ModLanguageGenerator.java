package net.flandre923.tutorialmod.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.flandre923.tutorialmod.block.ModBlocks;
import net.flandre923.tutorialmod.item.ModItemGroups;
import net.flandre923.tutorialmod.item.ModItems;

public class ModLanguageGenerator extends FabricLanguageProvider {
    public ModLanguageGenerator(FabricDataOutput dataOutput) {
        super(dataOutput,"en_us");
    }

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder) {
        translationBuilder.add(ModItems.RAW_RUBY,"Raw Ruby");
        translationBuilder.add(ModItems.RUBY,"Ruby");
        translationBuilder.add(ModItems.METAL_DETECTOR,"Metal Detector");
        translationBuilder.add(ModBlocks.RUBY_BLOCK,"Ruby Block");
        translationBuilder.add(ModBlocks.RAW_RUBY_BLOCK,"Raw Ruby Block");
        translationBuilder.add(ModBlocks.RUBY_ORE,"Ruby Ore");
        translationBuilder.add(ModBlocks.DEEPSLATE_RUBY_ORE,"Deepslate Ruby Ore");
        translationBuilder.add(ModBlocks.END_STONE_RUBY_ORE,"End Stone Ruby Ore");
        translationBuilder.add(ModBlocks.NETHER_RUBY_ORE,"Nether Ruby Ore");
        translationBuilder.add(ModBlocks.SIMPLE_BLOCK,"Simple Block");
        translationBuilder.add(ModItemGroups.TUTORIAL_TAB,"Tutorial Mod Tab");
    }
}
