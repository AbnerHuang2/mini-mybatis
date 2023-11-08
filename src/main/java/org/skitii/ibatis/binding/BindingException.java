package org.skitii.ibatis.binding;

import org.skitii.ibatis.exceptions.PersistenceException;

/**
 * @author skitii
 * @since 2023/11/08
 **/
public class BindingException extends PersistenceException {
    public BindingException() {
        super();
    }

    public BindingException(String message) {
        super(message);
    }

    public BindingException(String message, Throwable cause) {
        super(message, cause);
    }

    public BindingException(Throwable cause) {
        super(cause);
    }
}
