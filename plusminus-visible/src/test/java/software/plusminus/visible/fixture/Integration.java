package software.plusminus.visible.fixture;

import lombok.Data;
import lombok.EqualsAndHashCode;
import software.plusminus.visible.annotation.Visible;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Covers every way a property can be spelled: a field open to everyone, a field open to a role, a
 * field that says nothing and inherits {@link Base}'s role, and a computed getter that carries
 * the annotation because it has no field to put it on.
 *
 * <p>Reads of the secret are counted so a test can prove a hidden property's getter is never
 * called - which is what keeps a hidden lazy JPA association unfetched.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Integration extends Base {

    private static final AtomicInteger SECRET_READS = new AtomicInteger();

    @Visible(Visible.ALL)
    private String merchantLogin;

    private String merchantSecretKey;

    @Visible(Visible.ADMIN)
    private Long incomePrice;

    public static int secretReads() {
        return SECRET_READS.get();
    }

    public static void resetSecretReads() {
        SECRET_READS.set(0);
    }

    @Visible(Visible.ALL)
    public String getDisplayName() {
        return getMerchantLogin() + "'s shop";
    }

    public String getMerchantSecretKey() {
        SECRET_READS.incrementAndGet();
        return merchantSecretKey;
    }
}
