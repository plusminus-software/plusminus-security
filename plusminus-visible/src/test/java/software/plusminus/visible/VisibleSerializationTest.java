package software.plusminus.visible;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.Before;
import org.junit.Test;
import software.plusminus.context.Context;
import software.plusminus.security.Security;
import software.plusminus.visible.annotation.Visible;
import software.plusminus.visible.fixture.Child;
import software.plusminus.visible.fixture.Dto;
import software.plusminus.visible.fixture.PublicChild;
import software.plusminus.visible.jackson.VisibleSerializerModifier;
import software.plusminus.visible.service.VisibleService;

import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static software.plusminus.check.Checks.check;

@SuppressWarnings("checkstyle:ClassDataAbstractionCoupling")
public class VisibleSerializationTest {

    private Security security;
    private ObjectMapper mapper;

    @Before
    public void setUp() {
        security = null;
        Child.resetIncomeReads();
        // Not Context.of(supplier): that memoizes in a ThreadLocal, so the role would never change.
        Context<Security> context = () -> security;
        SimpleModule module = new SimpleModule("plusminus-visible");
        module.setSerializerModifier(new VisibleSerializerModifier(new VisibleService(context)));
        mapper = new ObjectMapper().registerModule(module);
    }

    /**
     * An anonymous caller sees what was opened to everyone and also what nobody restricted at
     * all. Only the members a role was named on stay out.
     */
    @Test
    public void showsUnrestrictedMembersToAnonymousCaller() {
        String json = json(child());

        check(json)
                .contains("\"merchantLogin\":\"shop\"")
                .contains("\"displayName\":\"shop's shop\"")
                .contains("\"merchantCity\":\"Kyiv\"")
                .doesNotContain("incomePrice")
                .doesNotContain("topSecret");
    }

    @Test
    public void doesNotInheritClassLevelRuleIntoSubclass() {
        String json = json(child());

        check(json).contains("merchantCity");
    }

    @Test
    public void letsAFieldOverrideItsClassLevelRule() {
        String json = json(child());

        check(json).contains("\"id\":7");
    }

    @Test
    public void readsTheAnnotationFromAGetter() {
        callerHasRoles(Visible.ADMIN);

        String json = json(child());

        check(json).contains("incomePrice");
    }

    @Test
    public void showsRestrictedMembersToThatRole() {
        callerHasRoles(Visible.ADMIN);

        String json = json(child());

        check(json)
                .contains("incomePrice")
                .contains("topSecret")
                .contains("internalNote");
    }

    @Test
    public void leavesUnannotatedClassesAlone() {
        String json = json(new Dto());

        check(json).contains("anything");
    }

    @Test
    public void matchesRolesIgnoringCase() {
        callerHasRoles("Admin");

        String json = json(child());

        check(json).contains("incomePrice");
    }

    @Test
    public void ignoresAnUnrelatedRole() {
        callerHasRoles("manager");

        String json = json(child());

        check(json).doesNotContain("incomePrice");
    }

    @Test
    public void keepsParentClassRuleInsideAMorePermissiveChild() {
        String json = json(publicChild());

        check(json).doesNotContain("internalNote");
    }

    @Test
    public void letsAChildTakeOverAPropertyByOverridingItsGetter() {
        String json = json(publicChild());

        check(json).contains("\"topSecret\":\"classified\"");
    }

    @Test
    public void doesNotWidenWhenTheOverridingChildStatesNoRule() {
        String json = json(child());

        check(json).doesNotContain("topSecret");
    }

    /**
     * The cache Jackson keeps of a class's serializer must not also cache who was asking. The
     * first call warms it as an admin; the second must still be filtered.
     */
    @Test
    public void doesNotLetTheFirstCallerFixTheShapeForTheNext() {
        callerHasRoles(Visible.ADMIN);
        String asAdmin = json(child());

        security = null;
        String asAnonymous = json(child());

        check(asAdmin).contains("incomePrice");
        check(asAnonymous).doesNotContain("incomePrice");
    }

    /** A hidden property must not call its getter -- that is what saves a lazy JPA association. */
    @Test
    public void neverReadsAHiddenProperty() {
        json(child());
        int readsWhileHidden = Child.incomeReads();

        callerHasRoles(Visible.ADMIN);
        json(child());

        check(readsWhileHidden).is(0);
        check(Child.incomeReads()).is(1);
    }

    private String json(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void callerHasRoles(String... roles) {
        Set<String> set = new HashSet<>();
        Collections.addAll(set, roles);
        security = Security.builder().username("someone").roles(set).build();
    }

    private Child child() {
        Child child = new Child();
        child.setId(7L);
        child.setMerchantLogin("shop");
        child.setMerchantCity("Kyiv");
        child.setIncomePrice(1000L);
        child.setTopSecret("classified");
        child.setInternalNote("note");
        return child;
    }

    private PublicChild publicChild() {
        PublicChild child = new PublicChild();
        child.setId(7L);
        child.setTopSecret("classified");
        child.setInternalNote("note");
        return child;
    }
}
