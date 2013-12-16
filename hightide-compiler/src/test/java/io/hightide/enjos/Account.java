package io.hightide.enjos;

import static io.hightide.enjos.AccountMetadata.*;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */

class Account {
    String number;
    String description;

    static {
        _id.nullable(false);
        _number.nullable(false).validate(number -> number.startsWith("123"));
        _description.nullable(true).length(15);
    }

   // public static AccountMetadata metadata = new AccountMetadata();
}
