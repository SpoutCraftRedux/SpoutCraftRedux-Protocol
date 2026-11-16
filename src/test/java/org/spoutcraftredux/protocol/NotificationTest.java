package org.spoutcraftredux.protocol;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.spoutcraftredux.protocol.message.Notification;
import org.junit.jupiter.api.Test;

class NotificationTest {
    @Test
    void roundTrips() {
        Notification n = new Notification("{\"text\":\"Hello\"}", "{\"text\":\"World\"}", "minecraft:diamond", 3000);
        assertEquals(n, Notification.decode(n.encode()));
    }

    @Test
    void zeroMeansDefaultDisplayTime() {
        assertEquals(7500, new Notification("\"a\"", "\"b\"", "minecraft:stone", 0).effectiveDisplayMillis());
        assertEquals(1200, new Notification("\"a\"", "\"b\"", "minecraft:stone", 1200).effectiveDisplayMillis());
    }

    @Test
    void rejectsOutOfRangeDisplayTime() {
        assertThrows(ProtocolException.class, () -> new Notification("\"a\"", "\"b\"", "minecraft:stone", -1));
        byte[] forged = new MessageWriter().writeString("\"a\"", 10).writeString("\"b\"", 10)
            .writeString("minecraft:stone", 32).writeVarInt(Integer.MAX_VALUE).toByteArray();
        assertThrows(ProtocolException.class, () -> Notification.decode(forged));
    }
}
