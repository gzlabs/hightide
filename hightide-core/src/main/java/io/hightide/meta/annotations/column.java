package io.hightide.meta.annotations;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public @interface column {
    String value() default "";
    boolean nullable() default true;
}
