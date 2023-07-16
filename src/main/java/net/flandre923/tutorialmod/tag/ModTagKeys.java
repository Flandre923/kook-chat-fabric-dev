package net.flandre923.tutorialmod.tag;

import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModTagKeys {
    public static final TagKey<Block> NEEDS_TOOL_LEVEL_4 = TagKey.of(RegistryKeys.BLOCK,new Identifier("fabric","needs_tool_level_4"));
}
