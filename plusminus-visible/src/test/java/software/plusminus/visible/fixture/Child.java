package software.plusminus.visible.fixture;

import lombok.Data;
import lombok.EqualsAndHashCode;
import software.plusminus.visible.annotation.Visible;

import java.util.concurrent.atomic.AtomicInteger;

@Data
@EqualsAndHashCode(callSuper = true)
public class Child extends Parent {

    private static final AtomicInteger INCOME_READS = new AtomicInteger();

    private String merchantLogin;

    private String merchantCity;

    private Long incomePrice;

    public static int incomeReads() {
        return INCOME_READS.get();
    }

    public static void resetIncomeReads() {
        INCOME_READS.set(0);
    }

    public String getDisplayName() {
        return getMerchantLogin() + "'s shop";
    }

    @Visible(Visible.ADMIN)
    public Long getIncomePrice() {
        INCOME_READS.incrementAndGet();
        return incomePrice;
    }

    @Override
    public String getTopSecret() {
        String value = super.getTopSecret();
        return value == null ? "" : value;
    }
}
