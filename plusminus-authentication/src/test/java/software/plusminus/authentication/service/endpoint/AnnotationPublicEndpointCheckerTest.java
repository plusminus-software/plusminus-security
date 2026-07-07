package software.plusminus.authentication.service.endpoint;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.method.HandlerMethod;
import software.plusminus.authentication.annotation.Public;
import software.plusminus.context.Context;

import java.lang.reflect.Method;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AnnotationPublicEndpointCheckerTest {

    @Mock
    private Context<HandlerMethod> handlerMethodContext;
    @InjectMocks
    private AnnotationPublicEndpointChecker checker;

    @Test
    public void emptyContext() {
        when(handlerMethodContext.optional())
                .thenReturn(Optional.empty());
        assertThat(checker.isPublicEndpoint()).isFalse();
    }

    @Test
    public void publicMethod() throws NoSuchMethodException {
        when(handlerMethodContext.optional())
                .thenReturn(Optional.of(handler("publicMethod")));
        assertThat(checker.isPublicEndpoint()).isTrue();
    }

    @Test
    public void nonPublicMethod() throws NoSuchMethodException {
        when(handlerMethodContext.optional())
                .thenReturn(Optional.of(handler("privateMethod")));
        assertThat(checker.isPublicEndpoint()).isFalse();
    }

    private HandlerMethod handler(String methodName) throws NoSuchMethodException {
        Bean bean = new Bean();
        Method method = Bean.class.getMethod(methodName);
        return new HandlerMethod(bean, method);
    }

    static class Bean {
        @Public
        public void publicMethod() {
        }

        public void privateMethod() {
        }
    }
}
