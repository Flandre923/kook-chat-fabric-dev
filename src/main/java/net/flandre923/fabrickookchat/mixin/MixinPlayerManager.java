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
import org.spongepowered.asm.mixin.Unique;
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

/**
 * MixinPlayerManager 类是一个混合类，用于扩展 PlayerManager 类，以添加玩家连接和断开连接时的事件处理功能。该类通过与 Kook 服务器的集成，能够在玩家加入或离开游戏时发送相应的消息到 Kook 频道。
 */
@Mixin(PlayerManager.class)
public class MixinPlayerManager {
    /**
     * 当玩家连接到服务器时，此方法将被调用，以发送玩家加入游戏的消息到 Kook 频道。
     * @param connection ClientConnection 类型，表示客户端连接信息。
     * @param player ServerPlayerEntity 类型，表示连接的玩家实体。
     * @param clientData ConnectedClientData 类型，包含客户端数据的额外信息
     * @param ci CallbackInfo 类型，提供关于混入方法调用的额外信息。
     */
    @Inject(method = "onPlayerConnect", at = @At("RETURN"))
    private void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData,CallbackInfo ci){
        handlePlayerEvent(player,  0);
    }

    /**
     * 当玩家从服务器断开连接时，此方法将被调用，以发送玩家离开游戏的消息到 Kook 频道。
     * @param player ServerPlayerEntity 类型，表示断开的玩家实体。
     * @param ci CallbackInfo 类型，提供关于混入方法调用的额外信息。
     */
    @Inject(method = "remove", at = @At("RETURN"))
    private void remove(ServerPlayerEntity player, CallbackInfo ci) {
        handlePlayerEvent(player, 1);
    }

    /**
     * 此方法用于处理玩家事件，如玩家连接和断开连接。
     * @param player ServerPlayerEntity 类型，表示相关的玩家实体。
     * @param type int 类型，表示事件类型（0 表示连接，1 表示断开连接）。
     */
    @Unique
    private void handlePlayerEvent(ServerPlayerEntity player,  int type) {
        if (shouldSendMessage(type)) {
            CompletableFuture.runAsync(() -> {
                KBCClient kbcClient = KookServerMod.getKbcClient();
                Channel channel = kbcClient.getCore().getHttpAPI().getChannel(KookServerMod.config.channel_ID);
                if (channel instanceof TextChannel) {
                    TextChannel textChannel = (TextChannel) channel;
                    textChannel.sendComponent(buildCard(player.getGameProfile().getName(), player.getUuid().toString(), type));
                }
            });
        }
    }

    /**
     * 此方法用于确定是否应该发送消息到 Kook 频道
     * @param type type: int 类型，表示事件类型（0 表示连接，1 表示断开连接）。
     * @return boolean 类型，如果应该发送消息，则返回 true，否则返回
     */
    @Unique
    private boolean shouldSendMessage(int type) {
        return type == 0 ? KookServerMod.config.join_Message : KookServerMod.config.quit_Message;
    }

    /**
     * 此方法用于创建一个包含玩家信息和事件类型（加入或离开）的卡片组件，用于在 Kook 频道上显示。
     * @param playerName playerName: String 类型，表示玩家的名称。
     * @param playerUUID playerUUID: String 类型，表示玩家的唯一标识符。
     * @param type int 类型，表示事件类型（0 表示连接，1 表示断开连接）。
     * @return MultipleCardComponent 类型，表示构建好的卡片组件。
     */
    @Unique
    private static MultipleCardComponent buildCard(String playerName, String playerUUID, int type) {
        Map<String, String> map = MapUtil.builder(new HashMap<String,String>()).put("playerName", playerName).map();
        return createCardBuilder(type).addModule(new SectionModule(
                        new MarkdownElement(StrUtil.format(getConfigMessage(type), map)),
                        new ImageElement(PlayerIcon.getPlayerIconUr(playerUUID), null, Size.SM, false),
                        Accessory.Mode.LEFT))
                .build();
    }

    /**
     * 此方法用于创建一个卡片构建器，用于构建包含玩家信息和事件类型（加入或离开）的卡片。
     * @param type type: int 类型，表示事件类型（0 表示连接，1 表示断开连接）。
     * @return CardBuilder 类型，表示创建好的卡片构建器。
     */
    @Unique
    private static CardBuilder createCardBuilder(int type) {
        return new CardBuilder().setTheme(getTheme(type)).setSize(Size.LG);
    }

    /**
     * 此方法用于获取卡片组件的主题，根据事件类型（加入或离开）确定。
     * @param type type: int 类型，表示事件类型（0 表示连接，1 表示断开连接）。
     * @return Theme 类型，表示卡片组件的主题。
     */
    @Unique
    private static Theme getTheme(int type) {
        return type == 0 ? Theme.SUCCESS : Theme.DANGER;
    }

    /**
     * 此方法用于获取卡片组件中使用的消息模板，根据事件类型（加入或离开）确定。
     * @param type  int 类型，表示事件类型（0 表示连接，1 表示断开连接）。
     * @return String 类型，表示用于构建卡片消息的模板。
     */
    @Unique
    private static String getConfigMessage(int type) {
        return type == 0 ? KookServerMod.config.player_Join_Message : KookServerMod.config.player_Quit_Message;
    }


}
