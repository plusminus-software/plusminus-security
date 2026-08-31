package software.plusminus.visible.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Who may see this field in JSON.
 *
 * <pre>
 *   &#64;Visible(Visible.ALL)          // everyone, even an anonymous caller
 *   &#64;Visible(Visible.ADMIN)        // only administrators
 *   &#64;Visible({"admin", "owner"})    // any one of them
 * </pre>
 *
 * <p>A member nobody restricted is serialized. A restriction comes from the member's own
 * annotation or, failing that, from a class-level annotation on the class that <em>declares</em>
 * the member, which covers everything that class declares without saying otherwise. A member
 * always overrides its class, so a field can open itself to {@link #ALL} inside a class that is
 * otherwise admin-only.
 *
 * <p>A class-level annotation stops at the class it is written on. A subclass does not pick it
 * up, and the members a subclass declares stay unrestricted unless the subclass says so itself -
 * a base entity cannot silently hide fields somebody added downstream. A subclass that opens
 * itself up does not thereby open what its base restricted, either: the base still declares
 * those members, so the base's rule is the one that applies to them.
 *
 * <pre>
 *   &#64;Visible(Visible.ADMIN)         &#64;Visible(Visible.ALL)
 *   class Base {                     class Child extends Base {
 *       Long id;      // admin           String own;   // everyone
 *       String note;  // admin       }                 // note stays admin
 *   }
 * </pre>
 *
 * <p>A subclass takes one of those members over by declaring a member of it itself - overriding
 * the getter - after which the subclass's rule applies instead. It has to state a rule of its own
 * to do so, so overriding a getter for an unrelated reason cannot quietly widen what a base class
 * restricted.
 *
 * <p>This filters serialization only. The column is still read from the database, and a caller
 * who may not see a field may still be able to write it.
 */
@Documented
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Visible {

    /** Everyone, authenticated or not. Nothing grants this role; it is recognised. */
    String ALL = "#all";

    /**
     * The administrator role, spelled once here rather than as a literal at every field that
     * needs it. An application that names its administrators something else keeps using its own
     * string; this is a convenience, not a role the library grants.
     */
    String ADMIN = "admin";

    /**
     * Roles that may see it, any one of which is enough. Compared without regard to case.
     *
     * @return the roles allowed to see the annotated member
     */
    String[] value();

}
