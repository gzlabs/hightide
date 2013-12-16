package io.hightide.validation;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class FieldValidator {

    private String field;

    private boolean nullable;

    public FieldValidator(String field, boolean nullable) {
        this.field = field;
        this.nullable = nullable;
    }

    public String getField() {
        return field;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }
}
