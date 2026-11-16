package org.spoutcraftredux.protocol;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.spoutcraftredux.protocol.content.ContentKeys;
import org.spoutcraftredux.protocol.message.Content;
import org.spoutcraftredux.protocol.message.ContentAck;

class ContentTest {
    @Test
    void contentRoundTrips() {
        Content content = new Content(List.of(
            new Content.Entry(ContentKeys.ITEM, "test:ruby", 1505, Map.of(
                ContentKeys.NAME, "{\"text\":\"Ruby\"}", ContentKeys.MAX_STACK_SIZE, 16, ContentKeys.RARITY, "rare")),
            new Content.Entry(ContentKeys.ITEM, "test:snack", 1506, Map.of(
                ContentKeys.NUTRITION, 4, ContentKeys.SATURATION, 0.6f, ContentKeys.CAN_ALWAYS_EAT, true))
        ));
        Content decoded = Content.decode(content.encode());
        assertEquals(content, decoded);
        assertEquals(0.6f, decoded.entries().get(1).number(ContentKeys.SATURATION, 0));
    }

    @Test
    void ackRoundTrips() {
        assertTrue(ContentAck.decode(new ContentAck("").encode()).ok());
        ContentAck failed = new ContentAck("Item ids don't match");
        assertFalse(ContentAck.decode(failed.encode()).ok());
        assertEquals(failed, ContentAck.decode(failed.encode()));
    }

    @Test
    void rejectsNegativeRawId() {
        assertThrows(ProtocolException.class, () -> new Content.Entry(ContentKeys.ITEM, "a:b", -1, Map.of()));
    }

    @Test
    void rejectsTooManyEntries() {
        byte[] forged = new MessageWriter().writeVarInt(Limits.MAX_CONTENT_ENTRIES + 1).toByteArray();
        assertThrows(ProtocolException.class, () -> Content.decode(forged));
    }
}
