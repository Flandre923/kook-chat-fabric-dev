package net.flandre923.tutorialmod.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.flandre923.tutorialmod.TutorialMod;
import net.flandre923.tutorialmod.block.ModBlocks;
import net.flandre923.tutorialmod.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.data.client.*;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class ModModelGenerator extends FabricModelProvider {
    public ModModelGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.RUBY_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.RAW_RUBY_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.RUBY_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DEEPSLATE_RUBY_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.NETHER_RUBY_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.END_STONE_RUBY_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.SIMPLE_BLOCK);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.RUBY, Models.GENERATED);
        itemModelGenerator.register(ModItems.RAW_RUBY, Models.GENERATED);
        itemModelGenerator.register(ModItems.METAL_DETECTOR, Models.GENERATED);
        itemModelGenerator.register(ModBlocks.RAW_RUBY_BLOCK.asItem(),registerItemBlockModel(ModBlocks.RAW_RUBY_BLOCK));
        itemModelGenerator.register(ModBlocks.RUBY_BLOCK.asItem(), registerItemBlockModel(ModBlocks.RUBY_BLOCK));
        itemModelGenerator.register(ModBlocks.RUBY_ORE.asItem(), registerItemBlockModel(ModBlocks.RUBY_ORE));
        itemModelGenerator.register(ModBlocks.DEEPSLATE_RUBY_ORE.asItem(), registerItemBlockModel(ModBlocks.DEEPSLATE_RUBY_ORE));
        itemModelGenerator.register(ModBlocks.NETHER_RUBY_ORE.asItem(), registerItemBlockModel(ModBlocks.NETHER_RUBY_ORE));
        itemModelGenerator.register(ModBlocks.END_STONE_RUBY_ORE.asItem(), registerItemBlockModel(ModBlocks.END_STONE_RUBY_ORE));
        itemModelGenerator.register(ModBlocks.SIMPLE_BLOCK.asItem(), registerItemBlockModel(ModBlocks.SIMPLE_BLOCK));
    }

    private static Model registerItemBlockModel(Block parent, TextureKey ... requiredTextureKeys) {
        String name = ModelIds.getBlockModelId(parent).getPath();
        return new Model(Optional.of(new Identifier(TutorialMod.MOD_ID, name)), Optional.empty(), requiredTextureKeys);
    }
}

