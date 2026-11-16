package org.spoutcraftredux.protocol;

/** Feature flags exchanged in the handshake; features are gated on these, not on the version. */
public final class Capabilities {
    public static final String UI = "ui.v1";
    public static final String HUD = "hud.v1";
    public static final String SKY = "sky.v1";
    public static final String KEYBIND = "keybind.v1";
    public static final String NOTIFICATION = "notification.v1";
    public static final String ASSET = "asset.v1";

    private Capabilities() {
    }
}
