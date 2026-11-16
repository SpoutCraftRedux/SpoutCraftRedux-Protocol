package org.spoutcraftredux.protocol;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.spoutcraftredux.protocol.message.AssetData;
import org.spoutcraftredux.protocol.message.AssetRequest;

class AssetMessagesTest {
    private static final String HASH = "a".repeat(64);

    @Test
    void roundTrips() {
        AssetRequest request = new AssetRequest(HASH);
        assertEquals(request, AssetRequest.decode(request.encode()));
        AssetData data = new AssetData(HASH, 10, 4, new byte[] {1, 2, 3});
        assertEquals(data, AssetData.decode(data.encode()));
    }

    @Test
    void rejectsBadHashesAndSizes() {
        assertThrows(ProtocolException.class, () -> new AssetRequest("../../etc/passwd"));
        assertThrows(ProtocolException.class, () -> new AssetData(HASH, 2, 1, new byte[] {1, 2}));
        assertThrows(ProtocolException.class, () -> new AssetData(HASH, AssetData.MAX_ASSET_BYTES + 1, 0, new byte[0]));
        byte[] forged = new MessageWriter().writeString(HASH, 64).writeVarInt(10).writeVarInt(0).writeVarInt(1000).toByteArray();
        assertThrows(ProtocolException.class, () -> AssetData.decode(forged));
    }
}
