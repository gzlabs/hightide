package io.hightide.validation;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class ValidatorBuilder {

    private String field;

    private boolean nullable;

    public ValidatorBuilder(String field) {
        this.field = field;
    }

    public static ValidatorBuilder Validator(String field) {
        return new ValidatorBuilder(field);
    }

    public ValidatorBuilder nullable(boolean nullable) {
        this.nullable = nullable;
        return this;
    }

    public FieldValidator validate() {
        return new FieldValidator(field, nullable);
    }
}
