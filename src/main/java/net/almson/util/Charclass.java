package net.almson.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author alex
 */
@Target ({ElementType.TYPE_USE})
@Retention (RetentionPolicy.CLASS)
@Expr
@interface Charclass {
    
}
