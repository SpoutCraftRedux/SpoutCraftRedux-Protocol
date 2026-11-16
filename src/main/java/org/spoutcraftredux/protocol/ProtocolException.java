package org.spoutcraftredux.protocol;

/** Thrown when a message is malformed or breaks a protocol limit. */
public final class ProtocolException extends RuntimeException {
    public ProtocolException(String message) {
        super(message);
    }
}
