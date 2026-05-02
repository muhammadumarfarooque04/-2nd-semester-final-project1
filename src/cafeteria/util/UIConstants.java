package cafeteria.util;

import java.awt.Color;
import java.awt.Font;

/**
 * UIConstants – Centralised style constants for consistent UI theming.
 * All colours, fonts and sizes are defined here to support reusability.
 *
 * Course: SW121 – Object Oriented Programming | Batch: K25SW
 */
public class UIConstants {

    // ── Colour Palette ───────────────────────────────────────────────────────
    public static final Color PRIMARY_COLOR     = new Color(41, 128, 185);   // Blue
    public static final Color SECONDARY_COLOR   = new Color(52, 73, 94);     // Dark
    public static final Color SUCCESS_COLOR     = new Color(39, 174, 96);    // Green
    public static final Color DANGER_COLOR      = new Color(192, 57, 43);    // Red
    public static final Color WARNING_COLOR     = new Color(243, 156, 18);   // Orange
    public static final Color LIGHT_BG          = new Color(236, 240, 241);  // Light grey
    public static final Color WHITE             = Color.WHITE;
    public static final Color HEADER_COLOR      = new Color(44, 62, 80);     // Dark navy

    // ── Fonts ────────────────────────────────────────────────────────────────
    public static final Font  TITLE_FONT        = new Font("Segoe UI", Font.BOLD,  24);
    public static final Font  HEADER_FONT       = new Font("Segoe UI", Font.BOLD,  16);
    public static final Font  BODY_FONT         = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font  BUTTON_FONT       = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font  SMALL_FONT        = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font  TABLE_HEADER_FONT = new Font("Segoe UI", Font.BOLD,  12);

    // ── Table Row Heights ────────────────────────────────────────────────────
    public static final int   TABLE_ROW_HEIGHT  = 28;

    /** Prevent instantiation. */
    private UIConstants() {}
}
