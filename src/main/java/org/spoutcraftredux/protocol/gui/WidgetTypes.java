package org.spoutcraftredux.protocol.gui;

/** Widget type names and the properties each one reads. */
public final class WidgetTypes {
    /** text (JSON), color (int ARGB), shadow (bool), align (int, see Anchors.ALIGN_*), wrap (bool). */
    public static final String LABEL = "label";
    /** text (JSON), align (int). Sends CLICK. */
    public static final String BUTTON = "button";
    /** text (JSON), checked (bool). Sends CHECK. */
    public static final String CHECKBOX = "checkbox";
    /** text (string), placeholder (JSON), max_length (int). Sends TEXT while typing and SUBMIT on Enter. */
    public static final String TEXT_FIELD = "text_field";
    /** text (JSON), position (float 0 to 1). Sends SLIDE. */
    public static final String SLIDER = "slider";
    /** top (int ARGB), bottom (int ARGB). */
    public static final String GRADIENT = "gradient";
    /** item (string item id). */
    public static final String ITEM = "item";
    /** asset (SHA-256 hex of a PNG the client fetches with AssetRequest). Drawn stretched to the widget's size. */
    public static final String TEXTURE = "texture";
    /** titles and texts (lists of JSON, one per row), selected (int, -1 for none). Sends SELECT. */
    public static final String LIST = "list";

    private WidgetTypes() {
    }
}
