package net.flandre923.fabrickookchat.mixin;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import net.flandre923.fabrickookchat.KookMod;
import net.flandre923.fabrickookchat.KookServerMod;
import net.minecraft.SharedConstants;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ServerCommonPacketListener;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.listener.TickablePacketListener;
import net.minecraft.network.message.*;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.network.*;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import snw.jkook.entity.channel.Channel;
import snw.jkook.entity.channel.TextChannel;
import snw.kookbc.impl.KBCClient;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * MixinServerPlayNetworkHandler 是一个抽象的混入类，它扩展了 ServerCommonNetworkHandler 类，并实现了 ServerPlayPacketListener, PlayerAssociatedNetworkHandler, ServerCommonPacketListener, 和 TickablePacketListener 接口。该类主要用于处理《我的世界》服务器中的玩家聊天消息，并在必要时将消息转发到 Kook 频道。
 */
@Mixin(ServerPlayNetworkHandler.class)
public abstract class MixinServerPlayNetworkHandler extends ServerCommonNetworkHandler implements ServerPlayPacketListener, PlayerAssociatedNetworkHandler, ServerCommonPacketListener, TickablePacketListener {
    @Shadow
    private ServerPlayerEntity player;

    /**
     * 不可实例化
     * @param server
     * @param connection
     * @param clientData
     */
    public MixinServerPlayNetworkHandler(MinecraftServer server, ClientConnection connection, ConnectedClientData clientData) {
        super(server, connection, clientData);
    }

    @Shadow
    public abstract  Optional<LastSeenMessageList> validateAcknowledgment(LastSeenMessageList.Acknowledgment acknowledgment);

    @Shadow
    public abstract SignedMessage getSignedMessage(ChatMessageC2SPacket packet, LastSeenMessageList lastSeenMessages) throws MessageChain.MessageChainException;

    @Shadow
    public abstract void handleMessageChainException(MessageChain.MessageChainException exception);

    /**
     * 当玩家发送聊天消息时，此方法将被调用。它首先检查消息中是否含有非法字符，如果有，则断开玩家的连接。如果没有，则处理聊天消息。
     * @param packet ChatMessageC2SPacket 类型，包含玩家发送的聊天消息。
     * @param ci CallbackInfo 类型，提供关于混入方法调用的额外信息。
     */
    @Inject(method = "onChatMessage", at = @At("HEAD"), cancellable = true)
    private void onChatMessage(ChatMessageC2SPacket packet, CallbackInfo ci) {
//        if (hasIllegalCharacter(packet.chatMessage())) {
//            disconnect(Text.translatable("multiplayer.disconnect.illegal_characters"));
//        } else {
            handleChatMessage(packet);
//        }
        ci.cancel();

    }

    /**
     * 此方法用于处理玩家发送的聊天消息，包括将其转发到 Kook 频道和广播到《我的世界》服务器。
     * @param packet ChatMessageC2SPacket 类型，包含玩家发送的聊天消息。
     */
    @Unique
    private void handleChatMessage(ChatMessageC2SPacket packet) {
        Optional<LastSeenMessageList> optional = this.validateAcknowledgment(packet.acknowledgment());
        if (optional.isPresent()) {
            sendMessageToKook(packet.chatMessage());
            broadcastMessageToMinecraft(packet, optional.get());
        }
    }

    /**
     * 此方法用于将玩家发送的聊天消息转发到 Kook 频道。
     * @param message String 类型，表示玩家发送的聊天消息。
     */
    @Unique
    private void sendMessageToKook(String message) {
        if (KookServerMod.config.to_Kook) {
            CompletableFuture.runAsync(() -> {
                KBCClient kbcClient = KookServerMod.getKbcClient();
                Channel channel = kbcClient.getCore().getHttpAPI().getChannel(KookServerMod.config.channel_ID);
                if (channel instanceof TextChannel) {
                    Map<String, String> map = MapUtil.builder(new HashMap<String,String>()).put("playerName", player.getGameProfile().getName()).put("message", message).map();
                    TextChannel textChannel = (TextChannel) channel;
                    textChannel.sendComponent(StrUtil.format(KookServerMod.config.to_Kook_Message, map));
                }
            });
        }
    }

    /**
     * 此方法用于在《我的世界》服务器上广播玩家发送的聊天消息。
     * @param packet ChatMessageC2SPacket 类型，包含玩家发送的聊天消息
     * @param lastSeenMessages LastSeenMessageList 类型，表示消息链的确认信息。
     */
    @Unique
    private void broadcastMessageToMinecraft(ChatMessageC2SPacket packet, LastSeenMessageList lastSeenMessages) {
        try {
            SignedMessage signedMessage = getSignedMessage(packet, lastSeenMessages);
            server.getPlayerManager().broadcast(signedMessage.withUnsignedContent(Objects.requireNonNull(Text.Serialization.fromJson("[{\"text\":\"" + packet.chatMessage() + "\"}]",player.getWorld().getRegistryManager()))), player, MessageType.params(MessageType.CHAT, player));
        } catch (MessageChain.MessageChainException e) {
            handleMessageChainException(e);
        }
    }

//    /**
//     * 此方法用于检查玩家发送的聊天消息中是否含有非法字符。
//     * @param message String 类型，表示玩家发送的聊天消息。
//     * @return boolean 类型，如果消息中包含非法字符，则返回 true，否则返回 false。
//     */
//    @Unique
//    private boolean hasIllegalCharacter(String message) {
//        for (int i = 0; i < message.length(); ++i) {
//            if (!SharedConstants.isValidChar(message.charAt(i))) {
//                return true;
//            }
//        }
//        return false;
//    }
}


