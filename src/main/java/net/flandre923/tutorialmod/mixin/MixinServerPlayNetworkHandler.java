package net.flandre923.tutorialmod.mixin;

import net.flandre923.tutorialmod.TutorialMod;
import net.minecraft.SharedConstants;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.message.*;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.EntityTrackingListener;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListMap;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class MixinServerPlayNetworkHandler implements EntityTrackingListener, ServerPlayPacketListener {
    @Shadow
    private ServerPlayerEntity player;

    @Final
    @Shadow
    private MinecraftServer server;

    @Final
    @Shadow
    private MessageChainTaskQueue messageChainTaskQueue;

    @Shadow
    public abstract void checkForSpam();

    @Shadow
    public abstract void disconnect(Text reason);

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


                TutorialMod.LOGGER.info("playerName" + player.getName() + "  chat content : "+contentToMinecraft);

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


