package net.flandre923.tutorialmod.block.custom;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;


public class SimpleBlock extends Block {
    public SimpleBlock() {
        super(FabricBlockSettings.of().strength(3.5F).requiresTool()
                .sounds(BlockSoundGroup.METAL)
                .ticksRandomly());
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);
        world.addParticle(ParticleTypes.SMOKE,pos.getX()+.5,pos.getY()+1.5,pos.getZ()+.5,0,0,0);
    }


    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient){
            world.createExplosion(null,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,2f,false,World.ExplosionSourceType.MOB);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}


