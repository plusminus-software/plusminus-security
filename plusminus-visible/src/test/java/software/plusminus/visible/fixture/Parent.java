package software.plusminus.visible.fixture;

import lombok.Data;
import software.plusminus.visible.annotation.Visible;

@Visible(Visible.ADMIN)
@Data
public class Parent {

    @Visible(Visible.ALL)
    private Long id;

    private String topSecret;

    private String internalNote;

}
