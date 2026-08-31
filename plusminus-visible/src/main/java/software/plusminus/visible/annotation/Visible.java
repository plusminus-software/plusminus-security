package software.plusminus.visible.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Who may see this field in JSON. Without it, the field is not serialized at all.
 *
 * <pre>
 *   &#64;Visible(Visible.ALL)          // everyone
 *   &#64;Visible(Visible.ADMIN)        // only administrators
 *   &#64;Visible({"admin", "owner"})    // any one of them
 * </pre>
 *
 * <p>On a class it covers every field the class and its subclasses declare. A field overrides its
 * class. There is no default: a public field says {@link #ALL} rather than saying nothing.
 *
 * <p>This filters serialization only. The column is still read from the database, and a caller who
 * may not see a field may still be able to write it.
 */
@Documented
@Inherited
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Visible {

    /** Everyone, authenticated or not. Nothing grants this role; it is recognised. */
    String ALL = "all";

    /**
     * The administrator role, spelled once here rather than as a literal at every field that
     * needs it. An application that names its administrators something else keeps using its own
     * string; this is a convenience, not a role the library grants.
     */
    String ADMIN = "admin";

    /** Roles that may see it, any one of which is enough. Compared without regard to case. */
    String[] value();

}
