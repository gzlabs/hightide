package io.hightide.enjos;

import io.hightide.meta.MetaObject;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class AccountMetadata extends MetaObject {
    static MetaField<Long> _id = new MetaField<>("id");
    static MetaField<String> _number = new MetaField<>("number");
    static MetaField<String> _description = new MetaField<>("description");
}
