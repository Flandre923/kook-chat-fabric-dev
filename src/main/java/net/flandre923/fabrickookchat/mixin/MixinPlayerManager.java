package net.flandre923.fabrickookchat.mixin;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import net.flandre923.fabrickookchat.KookMod;
import net.flandre923.fabrickookchat.KookServerMod;
import net.flandre923.fabrickookchat.util.PlayerIcon;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import snw.jkook.entity.abilities.Accessory;
import snw.jkook.entity.channel.Channel;
import snw.jkook.entity.channel.TextChannel;
import snw.jkook.message.component.card.CardBuilder;
import snw.jkook.message.component.card.MultipleCardComponent;
import snw.jkook.message.component.card.Size;
import snw.jkook.message.component.card.Theme;
import snw.jkook.message.component.card.element.ImageElement;
import snw.jkook.message.component.card.element.MarkdownElement;
import snw.jkook.message.component.card.module.SectionModule;
import snw.kookbc.impl.KBCClient;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


@Mixin(PlayerManager.class)
public class MixinPlayerManager {

    @Inject(method = "broadcast(Lnet/minecraft/network/message/SignedMessage;Lnet/minecraft/server/command/ServerCommandSource;Lnet/minecraft/network/message/MessageType$Parameters;)V", at = @At("RETURN"))
    private void broadcast(SignedMessage message, ServerCommandSource source, MessageType.Parameters params, CallbackInfo ci) {
        sendMessage(message.getSignedContent(), source.getName());
    }

    // 当玩家发送消息
    private void sendMessage(String content,String username){
        KookMod.LOGGER.info("MixPlayerManager " + content + " :: " + username);
    }


    @Inject(method = "onPlayerConnect", at = @At("RETURN"))
    private void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData,CallbackInfo ci){
        KookMod.LOGGER.info("join game name : " + player.getEntityName());
        if(KookServerMod.config.join_Message){
            CompletableFuture.runAsync(() -> {
                KBCClient kbcClient = KookServerMod.getKbcClient();
                Channel channel = kbcClient.getCore().getHttpAPI().getChannel(KookServerMod.config.channel_ID);
                if (channel instanceof TextChannel) {
                    TextChannel textChannel = (TextChannel) channel;
                    textChannel.sendComponent(buildCard(player.getEntityName(),player.getUuid().toString(),0));
                }
            });
        }
    }


    private static MultipleCardComponent buildCard(String playerName, String playerUUID,int type) {

        Map<String, String> map = MapUtil.builder(new HashMap<String, String>())
                .put("playerName", playerName)
                .map();

        if (type == 0) {
            return  new CardBuilder().setTheme(Theme.SUCCESS).setSize(Size.LG)
                    .addModule(new SectionModule(
                            new MarkdownElement(StrUtil.format(KookServerMod.config.player_Join_Message, map)),
                            new ImageElement(PlayerIcon.getPlayerIconUr(playerUUID), null, Size.SM, false),
                            Accessory.Mode.LEFT))
                    .build();

        }else{
            return  new CardBuilder().setTheme(Theme.DANGER).setSize(Size.LG)
                    .addModule(new SectionModule(
                            new MarkdownElement(StrUtil.format(KookServerMod.config.player_Quit_Message, map)),
                            new ImageElement(PlayerIcon.getPlayerIconUr(playerUUID), null, Size.SM, false),
                            Accessory.Mode.LEFT))
                    .build();
        }
    }


    @Inject(method = "remove", at = @At("RETURN"))
    private void remove(ServerPlayerEntity player, CallbackInfo ci) {
        KookMod.LOGGER.info("end game name : " + player.getEntityName());
        if (KookServerMod.config.quit_Message) {
            CompletableFuture.runAsync(() -> {
                KBCClient kbcClient = KookServerMod.getKbcClient();
                Channel channel = kbcClient.getCore().getHttpAPI().getChannel(KookServerMod.config.channel_ID);
                if (channel instanceof TextChannel) {
                    TextChannel textChannel = (TextChannel) channel;
                    textChannel.sendComponent(buildCard(player.getEntityName(), player.getUuid().toString(),1));
                }
            });
        }

    }

}
