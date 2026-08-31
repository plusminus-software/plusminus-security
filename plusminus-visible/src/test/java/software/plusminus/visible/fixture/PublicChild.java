package software.plusminus.visible.fixture;

import lombok.Data;
import lombok.EqualsAndHashCode;
import software.plusminus.visible.annotation.Visible;

/**
 * Opens itself to everyone while extending an admin-only parent. What {@link Parent} declares
 * stays admin-only, because a class-level rule covers what its own class declares - unless this
 * class takes the property over by declaring a member of it, as it does for {@code topSecret} by
 * overriding the getter, and does not for {@code internalNote}.
 */
@Visible(Visible.ALL)
@Data
@EqualsAndHashCode(callSuper = true)
public class PublicChild extends Parent {

    @Override
    public String getTopSecret() {
        String value = super.getTopSecret();
        return value == null ? "" : value;
    }
}
