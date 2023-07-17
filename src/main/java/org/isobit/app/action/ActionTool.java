package org.isobit.app.action;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
public @interface ActionTool{
    public static final int DIALOG=1;
    public static final int MODAL=2;
    public static final int TAB=4;
    public static final int MULTIPLE=8;
    Class target() default Object.class;
    String[] params() default {};
    String text() default "";
    String icon() default "";
    int mode() default 0;
    int mnemonic() default 0;
    String group() default "General";
    boolean allwaysVisible() default false;
    double position() default -1;
    
}
