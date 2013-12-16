package io.hightide.validation;

import io.hightide.resources.ResourceBase;
import org.junit.Test;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class ValidatorTest {

    @Test
    public void testResourceValidation() {
        ATestResource atr = new ATestResource();
        atr.validate();
    }

    class ATestResource extends ResourceBase {

        private Long id;

        private String name;

        private String description;
    }
}
