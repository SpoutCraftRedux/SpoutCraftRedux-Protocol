package org.spoutcraftredux.protocol.gui;

/**
 * Where a widget's x and y are measured from. With SCALE, x, y, width and height are on a 427 by 240 layout that
 * stretches to the screen.
 */
public final class Anchors {
    public static final int TOP_LEFT = 0;
    public static final int TOP_CENTER = 1;
    public static final int TOP_RIGHT = 2;
    public static final int CENTER_LEFT = 3;
    public static final int CENTER_CENTER = 4;
    public static final int CENTER_RIGHT = 5;
    public static final int BOTTOM_LEFT = 6;
    public static final int BOTTOM_CENTER = 7;
    public static final int BOTTOM_RIGHT = 8;
    public static final int SCALE = 9;

    public static final int SCALE_WIDTH = 427;
    public static final int SCALE_HEIGHT = 240;

    public static final int ALIGN_LEFT = 0;
    public static final int ALIGN_CENTER = 1;
    public static final int ALIGN_RIGHT = 2;

    private Anchors() {
    }
}
