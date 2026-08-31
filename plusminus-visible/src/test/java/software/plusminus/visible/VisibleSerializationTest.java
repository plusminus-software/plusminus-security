package software.plusminus.visible;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.Before;
import org.junit.Test;
import software.plusminus.context.Context;
import software.plusminus.security.Security;
import software.plusminus.visible.annotation.Visible;
import software.plusminus.visible.fixture.Integration;
import software.plusminus.visible.fixture.PlainDto;
import software.plusminus.visible.jackson.VisibleSerializerModifier;
import software.plusminus.visible.service.VisibilityService;

import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static software.plusminus.check.Checks.check;

public class VisibleSerializationTest {

    private Security security;
    private ObjectMapper mapper;

    @Before
    public void setUp() {
        security = null;
        Integration.resetSecretReads();
        // Not Context.of(supplier): that memoizes in a ThreadLocal, so the role would never change.
        Context<Security> context = () -> security;
        SimpleModule module = new SimpleModule("plusminus-visible");
        module.setSerializerModifier(new VisibleSerializerModifier(new VisibilityService(context)));
        mapper = new ObjectMapper().registerModule(module);
    }

    /**
     * An anonymous caller sees only what was opened to everyone: the field annotated with
     * {@link Visible#ALL}, and the getter that carries the annotation because it has no field to
     * put it on. Everything a role opened stays out, whether the role was named on the member or
     * inherited from the base class.
     */
    @Test
    public void showsOnlyPublicMembersToAnonymousCaller() {
        String json = json(integration());

        check(json)
                .contains("\"merchantLogin\":\"shop\"")
                .contains("\"displayName\":\"shop's shop\"")
                .doesNotContain("incomePrice")
                .doesNotContain("topSecret")
                .doesNotContain("merchantSecretKey");
    }

    /**
     * An admin additionally sees the field its own role opened, the one the base class declares,
     * and the unannotated field the base class's role covers by standing in for it.
     */
    @Test
    public void showsRoleMembersToThatRole() {
        security = withRoles(Visible.ADMIN);

        String json = json(integration());

        check(json)
                .contains("incomePrice")
                .contains("topSecret")
                .contains("merchantSecretKey");
    }

    /** A class that never mentions the annotation is somebody else's DTO, and is left alone. */
    @Test
    public void leavesUnannotatedClassesAlone() {
        String json = json(new PlainDto());

        check(json).contains("anything");
    }

    @Test
    public void matchesRolesIgnoringCase() {
        security = withRoles("Admin");

        String json = json(integration());

        check(json).contains("incomePrice");
    }

    @Test
    public void ignoresAnUnrelatedRole() {
        security = withRoles("manager");

        String json = json(integration());

        check(json).doesNotContain("incomePrice");
    }

    /**
     * The cache Jackson keeps of a class's serializer must not also cache who was asking. The
     * first call warms it as an admin; the second must still be filtered.
     */
    @Test
    public void doesNotLetTheFirstCallerFixTheShapeForTheNext() {
        security = withRoles(Visible.ADMIN);
        String asAdmin = json(integration());

        security = null;
        String asAnonymous = json(integration());

        check(asAdmin).contains("incomePrice");
        check(asAnonymous).doesNotContain("incomePrice");
    }

    /** A hidden property must not call its getter -- that is what saves a lazy JPA association. */
    @Test
    public void neverReadsAHiddenProperty() {
        json(integration());
        int readsWhileHidden = Integration.secretReads();

        security = withRoles(Visible.ADMIN);
        json(integration());

        check(readsWhileHidden).is(0);
        check(Integration.secretReads()).is(1);
    }

    private String json(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }

    // A fake secret is the whole point of the fixture: the test asserts it does not get out.
    @SuppressFBWarnings("HARD_CODE_PASSWORD")
    private Integration integration() {
        Integration integration = new Integration();
        integration.setMerchantLogin("shop");
        integration.setMerchantSecretKey("s3cr3t");
        integration.setIncomePrice(1000L);
        integration.setTopSecret("classified");
        return integration;
    }

    private Security withRoles(String... roles) {
        Set<String> set = new HashSet<>();
        Collections.addAll(set, roles);
        return Security.builder().username("someone").roles(set).build();
    }
}
