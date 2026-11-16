package org.spoutcraftredux.protocol;

/** Hard caps every decoder enforces. */
public final class Limits {
    public static final int MAX_CLIENTBOUND_BYTES = 1 << 20;
    public static final int MAX_SERVERBOUND_BYTES = 32 * 1024 - 1;
    public static final int MAX_CAPABILITIES = 64;
    public static final int MAX_CAPABILITY_LENGTH = 64;
    public static final int MAX_VERSION_STRING_LENGTH = 64;
    public static final int MAX_COMPONENT_JSON_LENGTH = 4096;
    public static final int MAX_IDENTIFIER_LENGTH = 256;
    public static final int MAX_KEY_BINDINGS = 256;
    public static final int MAX_PLUGIN_NAME_LENGTH = 64;

    private Limits() {
    }
}
