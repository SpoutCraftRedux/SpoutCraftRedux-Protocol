package org.spoutcraftredux.protocol;

import java.util.OptionalInt;

/** Protocol constants shared by the server and the client. */
public final class SpoutCraftReduxProtocol {
    public static final String NAMESPACE = "spoutcraftredux";

    /** Bump when a message changes incompatibly. */
    public static final int VERSION = 1;
    public static final int MIN_SUPPORTED_VERSION = 1;

    private SpoutCraftReduxProtocol() {
    }

    /** Picks the highest version both sides support, if any. */
    public static OptionalInt negotiate(int aMin, int aMax, int bMin, int bMax) {
        int high = Math.min(aMax, bMax);
        int low = Math.max(aMin, bMin);
        return high >= low ? OptionalInt.of(high) : OptionalInt.empty();
    }
}
