package me.lucyydotp.chatchain;

import net.minecraft.network.chat.MessageSignature;
import org.jetbrains.annotations.Nullable;

import java.util.Base64;

public class ChatChainDebug {

    private static final Base64.Encoder base64 = Base64.getEncoder();

    public static String pretty(@Nullable MessageSignature signature) {
        if (signature == null) return "UNKNOWN";
        return base64.encodeToString(signature.bytes());
    }
}
