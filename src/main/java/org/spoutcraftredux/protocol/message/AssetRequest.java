package org.spoutcraftredux.protocol.message;

import org.spoutcraftredux.protocol.MessageReader;
import org.spoutcraftredux.protocol.MessageWriter;
import org.spoutcraftredux.protocol.ProtocolException;

/**
 * Client to server: send me the asset with this hash. The client only asks for assets it has seen in a widget and
 * doesn't have cached.
 *
 * @param sha256 the asset's SHA-256 as 64 lowercase hex characters
 */
public record AssetRequest(String sha256) {
    public static final String CHANNEL = "spoutcraftredux:asset_request";

    public AssetRequest {
        if (!sha256.matches("[0-9a-f]{64}")) {
            throw new ProtocolException("Not a SHA-256 hash");
        }
    }

    public byte[] encode() {
        return new MessageWriter().writeString(this.sha256, 64).toByteArray();
    }

    public static AssetRequest decode(byte[] data) {
        MessageReader in = new MessageReader(data);
        AssetRequest request = new AssetRequest(in.readString(64));
        in.expectEnd();
        return request;
    }
}
