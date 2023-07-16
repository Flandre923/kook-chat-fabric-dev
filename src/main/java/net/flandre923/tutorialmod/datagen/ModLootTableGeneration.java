package net.flandre923.tutorialmod.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.flandre923.tutorialmod.block.ModBlocks;
import net.flandre923.tutorialmod.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.data.server.loottable.BlockLootTableGenerator;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;

public class ModLootTableGeneration extends FabricBlockLootTableProvider {
    public ModLootTableGeneration(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        addDrop(ModBlocks.RUBY_BLOCK);
        addDrop(ModBlocks.RAW_RUBY_BLOCK);
        addDrop(ModBlocks.SIMPLE_BLOCK);
        addDrop(ModBlocks.RUBY_ORE, this::rubyOreDrops);
        addDrop(ModBlocks.END_STONE_RUBY_ORE, this::rubyOreDrops);
        addDrop(ModBlocks.NETHER_RUBY_ORE, this::rubyOreDrops);
        addDrop(ModBlocks.DEEPSLATE_RUBY_ORE, this::rubyOreDrops);
    }


    public LootTable.Builder rubyOreDrops(Block drop) {
        return BlockLootTableGenerator.dropsWithSilkTouch(drop,
                (LootPoolEntry.Builder)this.applyExplosionDecay(drop,
                        ((LeafEntry.Builder) ItemEntry.builder(ModItems.RAW_RUBY).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0f, 5.0f)))).apply(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE))));
    }
}
