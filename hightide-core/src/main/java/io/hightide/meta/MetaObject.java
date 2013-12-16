package io.hightide.meta;

import java.util.function.Predicate;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class MetaObject {

    public static class MetaField<T> {
        private String field;

        private boolean nullable = true;

        private int length;

        private Predicate<T> predicate;

        public MetaField(String field) {
            this.field = field;
        }

        public MetaField<T> nullable(boolean nullable) {
            this.nullable = nullable;
            return this;
        }

        public MetaField<T> length(int length) {
            this.length = length;
            return this;
        }

        public MetaField<T> validate(Predicate<T> predicate) {
            this.predicate = predicate;
            return this;
        }
    }
}
