package net.flandre923.tutorialmod.mixin;

import net.flandre923.tutorialmod.TutorialMod;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(PlayerManager.class)
public class MixinPlayerManager {

    @Inject(method = "broadcast(Lnet/minecraft/network/message/SignedMessage;Lnet/minecraft/server/command/ServerCommandSource;Lnet/minecraft/network/message/MessageType$Parameters;)V", at = @At("RETURN"))
    private void broadcast(SignedMessage message, ServerCommandSource source, MessageType.Parameters params, CallbackInfo ci) {
        sendMessage(message.getSignedContent(), source.getName());
    }

    // 当玩家发送消息
    private void sendMessage(String content,String username){
        TutorialMod.LOGGER.info("MixPlayerManager " + content + " :: " + username);
    }


    @Inject(method = "onPlayerConnect", at = @At("RETURN"))
    private void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci){
        TutorialMod.LOGGER.info("join game name : " + player.getEntityName());
    }


    @Inject(method = "remove", at = @At("RETURN"))
    private void remove(ServerPlayerEntity player, CallbackInfo ci) {
        TutorialMod.LOGGER.info("end game name : " + player.getEntityName());
    }

}
