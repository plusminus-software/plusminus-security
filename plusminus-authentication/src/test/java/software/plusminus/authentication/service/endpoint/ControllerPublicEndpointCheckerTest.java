package software.plusminus.authentication.service.endpoint;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.method.HandlerMethod;
import software.plusminus.context.Context;

import java.lang.reflect.Method;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ControllerPublicEndpointCheckerTest {

    @Mock
    private Context<HandlerMethod> handlerMethodContext;
    @InjectMocks
    private ControllerPublicEndpointChecker checker;

    @Test
    public void emptyContext() {
        when(handlerMethodContext.optional()).thenReturn(Optional.empty());
        assertThat(checker.isPublicEndpoint()).isFalse();
    }

    @Test
    public void errorController() throws NoSuchMethodException {
        when(handlerMethodContext.optional()).thenReturn(Optional.of(handler(new ErrorBean())));
        assertThat(checker.isPublicEndpoint()).isTrue();
    }

    @Test
    public void regularController() throws NoSuchMethodException {
        when(handlerMethodContext.optional()).thenReturn(Optional.of(handler(new RegularBean())));
        assertThat(checker.isPublicEndpoint()).isFalse();
    }

    private HandlerMethod handler(Object bean) throws NoSuchMethodException {
        Method method = bean.getClass().getMethod("handle");
        return new HandlerMethod(bean, method);
    }

    static class ErrorBean implements ErrorController {
        public void handle() {
        }

        @Override
        public String getErrorPath() {
            return "/error";
        }
    }

    static class RegularBean {
        public void handle() {
        }
    }
}
