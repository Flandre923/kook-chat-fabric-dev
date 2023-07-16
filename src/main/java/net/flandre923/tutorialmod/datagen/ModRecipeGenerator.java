package net.flandre923.tutorialmod.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.flandre923.tutorialmod.block.ModBlocks;
import net.flandre923.tutorialmod.item.ModItems;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.recipe.book.RecipeCategory;

import java.util.List;
import java.util.function.Consumer;

public class ModRecipeGenerator extends FabricRecipeProvider {
    public ModRecipeGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.RUBY,9).input(ModBlocks.RUBY_BLOCK).criterion(FabricRecipeProvider.hasItem(ModItems.RUBY),
                FabricRecipeProvider.conditionsFromItem(ModItems.RUBY)).criterion(FabricRecipeProvider.hasItem(ModBlocks.RUBY_BLOCK),
                FabricRecipeProvider.conditionsFromItem(ModBlocks.RUBY_BLOCK)).offerTo(exporter);


        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.RUBY_BLOCK)
                .pattern("iii")
                .pattern("iii")
                .pattern("iii")
                .input('i', ModItems.RUBY)
                .criterion(FabricRecipeProvider.hasItem(ModItems.RUBY),
                        FabricRecipeProvider.conditionsFromItem(ModItems.RUBY))
                .offerTo(exporter);


        RecipeProvider.offerSmelting(exporter, List.of(ModItems.RAW_RUBY),RecipeCategory.MISC, ModItems.RUBY,0.045F,200,"ruby");
        RecipeProvider.offerBlasting(exporter, List.of(ModItems.RAW_RUBY),RecipeCategory.MISC, ModItems.RUBY,0.045F,100,"ruby");
    }
}
