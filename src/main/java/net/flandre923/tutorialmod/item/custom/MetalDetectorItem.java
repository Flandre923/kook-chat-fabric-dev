package net.flandre923.tutorialmod.item.custom;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

public class MetalDetectorItem extends Item {
    public MetalDetectorItem() {
        super(new FabricItemSettings().maxDamage(64));
    }


    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!context.getWorld().isClient){
            BlockPos pos = context.getBlockPos();
            PlayerEntity player = context.getPlayer();
            boolean foundBlock = false;

            for(int i =0;i<= pos.getY() + 64;i++){
                BlockState state = context.getWorld().getBlockState(pos.down(i));

                if(isValuableBlock(state)){
                    outputValuableCoordinates(pos.down(i),player,state.getBlock());
                    foundBlock = true;
                    break;
                }
            }

            if(!foundBlock){
                player.sendMessage(Text.literal("No Values Found"));
            }

            context.getStack().damage(1,context.getPlayer(),
                    playerEntity -> playerEntity.sendToolBreakStatus(playerEntity.getActiveHand()));

        }
        return ActionResult.SUCCESS;
    }
    private void outputValuableCoordinates(BlockPos blockPos, PlayerEntity player, Block block){
        player.sendMessage(Text.literal("Found " + block.asItem().getName().getString() + " at " +
                "(" + blockPos.getX() + " , " + blockPos.getY() + " , " + blockPos.getZ() +")"),false);
    }

    private boolean isValuableBlock(BlockState state){
        return state.isOf(Blocks.IRON_ORE) || state.isOf(Blocks.DIAMOND_ORE);
    }



}
