package me.lucyydotp.chatchain.mixin;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.MessageSignatureCache;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.stream.Collectors;

import static me.lucyydotp.chatchain.ChatChainDebug.pretty;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Shadow
    @Final
    private static Logger LOGGER;

    @Shadow
    private MessageSignatureCache messageSignatureCache;

    @Inject(
            method = "handlePlayerChat",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/chat/ChatListener;handleChatMessageError(Ljava/util/UUID;Lnet/minecraft/network/chat/ChatType$Bound;)V"
            )
    )
    private void logKnownMessageReferences(ClientboundPlayerChatPacket packet, CallbackInfo ci) {
        LOGGER.info("Signature is {}", pretty(packet.signature()));
        final var message = packet.body().lastSeen().entries()
                .stream()
                .map(e -> e.id() == -1 ?
                        "Signature " + pretty(e.fullSignature()) :
                        String.format("ID %s - %s", e.id(), pretty(messageSignatureCache.unpack(e.id())))
                )
                .collect(Collectors.joining("\n"));
        LOGGER.error("Chat message chain:\n{}", message);
    }
}
