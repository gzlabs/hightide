package io.hightide.devassist;

import actions.devassist.DefaultActions;
import io.hightide.Application;
import io.hightide.HightideExchange;
import io.hightide.HightideResponse;
import io.hightide.actions.InvocationAction;
import io.hightide.renderers.RythmHtmlRenderer;
import io.hightide.route.Route;
import org.junit.Test;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class DevaAssistTemplateRenderingTest {

    @Test
    public void testRenderingOfIndex() throws Exception {
        Application.run();
        HightideExchange ex = new HightideExchange();
        HightideResponse resp = new HightideResponse();
        resp.setReturnedObj("Hello World!");
        ex.setResponse(resp);
        InvocationAction ia = new InvocationAction();
        ia.setClazz(DefaultActions.class);
        ia.setMethod(DefaultActions.class.getDeclaredMethod("welcome"));
        ex.setResolvedRoute(new Route().invocationAction(ia));

        String renderedTemplate = new RythmHtmlRenderer().render(ex);

    }
}
