package org.isobit.util;

import java.awt.Dimension;
import java.awt.Insets;

public interface Constants {

    public static final String CLIENT_TYPE = "clientType";
    public static final String SEPARATOR = "#";
    public static final String SEPARATOR_ESCAPED = "\\#";
    public static final String EMPTY = "";
//0  0  0 saved none 	0
//0  1  0 saved modified	2
//
//1  0  0 insert none	4
//1  1  0 insert modified	6
    public final static short NONE = 0;
    public final static short MODIFIED = 2;
    public final static short INSERTED = 4;
    public final static short INSERTED_MODIFIED = 6;
    public final static short DELETE = 10;
    public final static short PREPARE = 11;
    public final static short UPDATE = 2;
    public final static short RETRIEVE = 301;
    public final static short DATA_UPDATED = 300;

    public static String TITLE_MESSAGES = "";
    public static final String HTTP_SUPPORT_HOST = "http.supportHost";
    public static final String FILE_SEPARATOR = "file.separator";
    public static final String NEW_LINE_STRING = System.getProperty("line.separator");
    public static final String QUOTE_STRING = "'";
    public static final char QUOTE_CHAR = '\'';
    public static final char NEW_LINE_CHAR = '\n';
    public static final char TAB_CHAR = '\t';
    public static final char COMMA_CHAR = ',';
    public static final String USER_HOME_DIR = "isobit.user.home.dir";
    public static final String USER_HOME = "user.home";
    public static final String FILTERS = "#filters";
    public static final String SORT = "_SORT";
    public static final String PARAMS = "#params";
    public static final String COUNT = "totalCount";
    public static final String Q = "#q";
    public static final String Q0 = "#q-0";
    public static final String USER = "#user";

    public static final String TABLE_TAG_START
            = "<table border='0' cellspacing='0' cellpadding='2'>";

    public static final String TABLE_TAG_END
            = "</table>";

    public static final Insets EMPTY_INSETS = new Insets(0, 0, 0, 0);

    public static final Dimension FORM_BUTTON_SIZE = new Dimension(100, 25);

    public static final String ACTION_CONF_PATH = "org/executequery/actions.xml";
    public static final String[] LOOK_AND_FEELS = {"Execute Query Default",
        "Smooth Gradient",
        "Bumpy Gradient",
        "Execute Query Theme",
        "Metal - Classic",
        "Metal - Ocean (JDK1.5+)",
        "CDE/Motif",
        "Windows",
        "GTK+",
        "Plugin"};

    public static final int EQ_DEFAULT_LAF = 0;
    public static final int SMOOTH_GRADIENT_LAF = 1;
    public static final int BUMPY_GRADIENT_LAF = 2;
    public static final int EQ_THM = 3;
    public static final int METAL_LAF = 4;
    public static final int OCEAN_LAF = 5;
    public static final int MOTIF_LAF = 6;
    public static final int WIN_LAF = 7;
    public static final int GTK_LAF = 8;
    public static final int PLUGIN_LAF = 9;

    //----------------------------
    // syntax colours and styles
    //----------------------------
    /**
     * Recognised syntax types
     */
    public static final String[] SYNTAX_TYPES = {"normal",
        "keyword",
        "quote",
        "singlecomment",
        "multicomment",
        "number",
        "operator",
        "braces",
        "literal",
        "braces.match1",
        "braces.error"};

    /**
     * The properties file style name prefix
     */
    public static final String STYLE_NAME_PREFIX = "sqlsyntax.style.";

    /**
     * The properties file style colour prefix
     */
    public static final String STYLE_COLOUR_PREFIX = "sqlsyntax.colour.";

    /**
     * The literal 'Plain'
     */
    public static final String PLAIN = "Plain";
    /**
     * The literal 'Italic'
     */
    public static final String ITALIC = "Italic";
    /**
     * The literal 'Bold'
     */
    public static final String BOLD = "Bold";

    //-------------------------
    // literal SQL keywords
    //-------------------------
    public static final String NULL_LITERAL = "NULL";
    public static final String TRUE_LITERAL = "TRUE";
    public static final String FALSE_LITERAL = "FALSE";

    public static final char[] BRACES = {'(', ')', '{', '}', '[', ']'};

    public static final String COLOUR_PREFERENCE = "colourPreference";

    public static final int DEFAULT_FONT_SIZE = 11;
    public static final Dimension BUTTON_SIZE = new Dimension(75, 26);

    public static final String[] TRANSACTION_LEVELS
            = {"TRANSACTION_NONE",
                "TRANSACTION_READ_UNCOMMITTED",
                "TRANSACTION_READ_COMMITTED",
                "TRANSACTION_REPEATABLE_READ",
                "TRANSACTION_SERIALIZABLE"};

    // Log4J logging levels
    public static final String[] LOG_LEVELS = {"INFO",
        "WARN",
        "DEBUG",
        "ERROR",
        "FATAL",
        "ALL"};

    /**
     * docked tab property keys public static final String[] DOCKED_TAB_KEYS = {
     * ConnectionsTreePanel.PROPERTY_KEY, DriversTreePanel.PROPERTY_KEY,
     * KeywordsDockedPanel.PROPERTY_KEY, SystemOutputPanel.PROPERTY_KEY};
     */
}
