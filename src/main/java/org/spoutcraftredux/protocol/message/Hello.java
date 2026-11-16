package org.spoutcraftredux.protocol.message;

import java.util.Set;
import org.spoutcraftredux.protocol.Limits;
import org.spoutcraftredux.protocol.MessageReader;
import org.spoutcraftredux.protocol.MessageWriter;
import org.spoutcraftredux.protocol.ProtocolException;

/** Server to client, sent during configuration to offer the SpoutCraft Redux protocol. */
public record Hello(int protocolMin, int protocolMax, Set<String> capabilities) {
    public static final String CHANNEL = "spoutcraftredux:hello";

    public Hello {
        if (protocolMin < 0 || protocolMax < protocolMin) {
            throw new ProtocolException("Bad version range " + protocolMin + ".." + protocolMax);
        }
        capabilities = Set.copyOf(capabilities);
    }

    public byte[] encode() {
        return new MessageWriter()
            .writeVarInt(protocolMin)
            .writeVarInt(protocolMax)
            .writeStrings(capabilities, Limits.MAX_CAPABILITIES, Limits.MAX_CAPABILITY_LENGTH)
            .toByteArray();
    }

    public static Hello decode(byte[] data) {
        MessageReader in = new MessageReader(data);
        Hello hello = new Hello(
            in.readVarInt(),
            in.readVarInt(),
            in.readStrings(Limits.MAX_CAPABILITIES, Limits.MAX_CAPABILITY_LENGTH)
        );
        in.expectEnd();
        return hello;
    }
}
