package org.spoutcraftredux.protocol;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.spoutcraftredux.protocol.message.Hello;
import org.spoutcraftredux.protocol.message.HelloAck;
import org.junit.jupiter.api.Test;

class HandshakeMessagesTest {
    @Test
    void helloRoundTrips() {
        Hello hello = new Hello(1, 3, Set.of(Capabilities.UI, Capabilities.SKY));
        assertEquals(hello, Hello.decode(hello.encode()));
    }

    @Test
    void helloAckRoundTrips() {
        HelloAck ack = new HelloAck(2, Set.of(Capabilities.HUD), "0.1.0+26.3");
        assertEquals(ack, HelloAck.decode(ack.encode()));
    }

    @Test
    void unicodeStringsRoundTrip() {
        HelloAck ack = new HelloAck(1, Set.of("ünï.v1"), "版本-1");
        assertEquals(ack, HelloAck.decode(ack.encode()));
    }

    @Test
    void rejectsTruncatedMessage() {
        byte[] full = new Hello(1, 1, Set.of(Capabilities.UI)).encode();
        byte[] cut = Arrays.copyOf(full, full.length - 1);
        assertThrows(ProtocolException.class, () -> Hello.decode(cut));
    }

    @Test
    void rejectsTrailingBytes() {
        byte[] full = new Hello(1, 1, Set.of()).encode();
        byte[] padded = Arrays.copyOf(full, full.length + 1);
        assertThrows(ProtocolException.class, () -> Hello.decode(padded));
    }

    @Test
    void rejectsTooManyCapabilities() {
        Set<String> many = IntStream.range(0, Limits.MAX_CAPABILITIES + 1)
            .mapToObj(i -> "cap" + i)
            .collect(Collectors.toSet());
        assertThrows(ProtocolException.class, () -> new Hello(1, 1, many).encode());

        byte[] forged = new MessageWriter().writeVarInt(1).writeVarInt(1).writeVarInt(10_000).toByteArray();
        assertThrows(ProtocolException.class, () -> Hello.decode(forged));
    }

    @Test
    void rejectsHugeStringLength() {
        byte[] forged = new MessageWriter().writeVarInt(1).writeVarInt(0).writeVarInt(Integer.MAX_VALUE).toByteArray();
        assertThrows(ProtocolException.class, () -> HelloAck.decode(forged));
    }

    @Test
    void rejectsOverlongVarInt() {
        byte[] forged = {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x01};
        assertThrows(ProtocolException.class, () -> new MessageReader(forged).readVarInt());
    }

    @Test
    void rejectsOversizedServerboundMessage() {
        assertThrows(ProtocolException.class, () -> HelloAck.decode(new byte[Limits.MAX_SERVERBOUND_BYTES + 1]));
    }

    @Test
    void varIntHandlesNegativeAndLargeValues() {
        for (int value : new int[] {0, 1, 127, 128, 300, Integer.MAX_VALUE, -1, Integer.MIN_VALUE}) {
            byte[] bytes = new MessageWriter().writeVarInt(value).toByteArray();
            assertEquals(value, new MessageReader(bytes).readVarInt());
        }
    }

    @Test
    void negotiatePicksHighestCommonVersion() {
        assertEquals(OptionalInt.of(3), SpoutCraftReduxProtocol.negotiate(1, 3, 2, 5));
        assertEquals(OptionalInt.of(2), SpoutCraftReduxProtocol.negotiate(2, 2, 1, 4));
        assertTrue(SpoutCraftReduxProtocol.negotiate(1, 2, 3, 4).isEmpty());
    }
}
