package software.plusminus.visible.fixture;

import lombok.Data;
import software.plusminus.visible.annotation.Visible;

/**
 * A base entity that opens itself to admins. Everything its subclasses declare without saying
 * otherwise falls back to this.
 */
@Visible(Visible.ADMIN)
@Data
public class Base {

    private String topSecret;

}
