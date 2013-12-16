package io.hightide.validation;

import java.lang.reflect.Field;
import java.util.function.Predicate;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class Constraint {
    private String id;
    private Field field;
    private Predicate predicate;

    public <F> Constraint(String id, Field field, Predicate<F> predicate) {
        this.id = id;
        this.field = field;
        this.predicate = predicate;
    }

    public Field getField() {
        return field;
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public String getKey() {
        return field.getDeclaringClass().getSimpleName() + "." + field.getName() + "." + id;
    }
}
