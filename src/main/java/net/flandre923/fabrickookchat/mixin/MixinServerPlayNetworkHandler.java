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

@Mixin(ServerPlayNetworkHandler.class)
public abstract class MixinServerPlayNetworkHandler extends ServerCommonNetworkHandler implements ServerPlayPacketListener, PlayerAssociatedNetworkHandler, ServerCommonPacketListener, TickablePacketListener {
    @Shadow
    private ServerPlayerEntity player;

//    @Final
//    @Shadow
//    private MinecraftServer server;

    @Final
    @Shadow
    private MessageChainTaskQueue messageChainTaskQueue;

    public MixinServerPlayNetworkHandler(MinecraftServer server, ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData) {
        super(server, connection, clientData);

    }

    @Shadow
    public abstract void checkForSpam();

//    @Shadow
//    public abstract void disconnect(Text reason);

    @Shadow
    public abstract CompletableFuture<FilteredMessage> filterText(String text);

    @Shadow
    public abstract Optional<LastSeenMessageList> validateMessage(String message, Instant timestamp, LastSeenMessageList.Acknowledgment acknowledgment);

    @Shadow
    public abstract void handleCommandExecution(CommandExecutionC2SPacket packet, LastSeenMessageList lastSeenMessages);

    @Shadow
    public abstract SignedMessage getSignedMessage(ChatMessageC2SPacket packet, LastSeenMessageList lastSeenMessages) throws MessageChain.MessageChainException;

    @Shadow
    public abstract void handleDecoratedMessage(SignedMessage message);

    @Shadow
    public abstract void handleMessageChainException(MessageChain.MessageChainException exception);

    @Inject(method = "onChatMessage",at=@At("HEAD"),cancellable = true)
    private void onChatMessage(ChatMessageC2SPacket packet, CallbackInfo ci){
        if(hasIllegalCharacter(packet.chatMessage())) {
            disconnect(Text.translatable("multiplayer.disconnect.illegal_characters"));
        }else{
            Optional<LastSeenMessageList> optional = validateMessage(packet.chatMessage(), packet.timestamp(), packet.acknowledgment());
            if (optional.isPresent()) {
                String contentToDiscord = packet.chatMessage();
                String contentToMinecraft = packet.chatMessage();


                KookMod.LOGGER.info("playerName" + player.getName() + "  chat content : "+contentToMinecraft);

                if (KookServerMod.config.to_Kook) {
                    CompletableFuture.runAsync(() -> {
                        KBCClient kbcClient = KookServerMod.getKbcClient();
                        Channel channel = kbcClient.getCore().getHttpAPI().getChannel(KookServerMod.config.channel_ID);
                        if (channel instanceof TextChannel) {

                            Map<String, String> map = MapUtil.builder(new HashMap<String, String>())
                                    .put("playerName", player.getEntityName())
                                    .put("message", contentToDiscord)
                                    .map();

                            TextChannel textChannel = (TextChannel) channel;
                            textChannel.sendComponent(StrUtil.format(KookServerMod.config.to_Kook_Message, map));
                        }
                    });
                }

                try {
                    SignedMessage signedMessage = getSignedMessage(packet, optional.get());
                    server.getPlayerManager().broadcast(signedMessage.withUnsignedContent(Objects.requireNonNull(Text.Serializer.fromJson("[{\"text\":\"" + contentToMinecraft + "\"}]"))), player, MessageType.params(MessageType.CHAT, player));
                } catch (MessageChain.MessageChainException e) {
                    handleMessageChainException(e);
                }

            }
        }
        ci.cancel();
    }

    private boolean hasIllegalCharacter(String message) {
        for (int i = 0; i < message.length(); ++i) {
            if (!SharedConstants.isValidChar(message.charAt(i))) {
                return true;
            }
        }

        return false;
    }
}


