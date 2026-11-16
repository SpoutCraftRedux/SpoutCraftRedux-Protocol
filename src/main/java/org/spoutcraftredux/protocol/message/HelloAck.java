package org.spoutcraftredux.protocol.message;

import java.util.Set;
import org.spoutcraftredux.protocol.Limits;
import org.spoutcraftredux.protocol.MessageReader;
import org.spoutcraftredux.protocol.MessageWriter;
import org.spoutcraftredux.protocol.ProtocolException;

/** Client to server: the chosen version and what the client supports. */
public record HelloAck(int protocol, Set<String> capabilities, String modVersion) {
    public static final String CHANNEL = "spoutcraftredux:hello_ack";

    public HelloAck {
        if (protocol < 0) {
            throw new ProtocolException("Bad protocol " + protocol);
        }
        capabilities = Set.copyOf(capabilities);
    }

    public byte[] encode() {
        byte[] bytes = new MessageWriter()
            .writeVarInt(protocol)
            .writeStrings(capabilities, Limits.MAX_CAPABILITIES, Limits.MAX_CAPABILITY_LENGTH)
            .writeString(modVersion, Limits.MAX_VERSION_STRING_LENGTH)
            .toByteArray();
        if (bytes.length > Limits.MAX_SERVERBOUND_BYTES) {
            throw new ProtocolException("HelloAck too large");
        }
        return bytes;
    }

    public static HelloAck decode(byte[] data) {
        if (data.length > Limits.MAX_SERVERBOUND_BYTES) {
            throw new ProtocolException("HelloAck too large");
        }
        MessageReader in = new MessageReader(data);
        HelloAck ack = new HelloAck(
            in.readVarInt(),
            in.readStrings(Limits.MAX_CAPABILITIES, Limits.MAX_CAPABILITY_LENGTH),
            in.readString(Limits.MAX_VERSION_STRING_LENGTH)
        );
        in.expectEnd();
        return ack;
    }
}
