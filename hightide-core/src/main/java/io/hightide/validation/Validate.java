package io.hightide.validation;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public @interface Validate {
    boolean nullable() default false;
}
