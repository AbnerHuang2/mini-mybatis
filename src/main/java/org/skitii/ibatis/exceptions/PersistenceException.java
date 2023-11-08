package org.skitii.ibatis.exceptions;

/**
 * @author skitii
 * @since 2023/11/08
 **/
public class PersistenceException extends IbatisException {
    public PersistenceException() {
        super();
    }

    public PersistenceException(String message) {
        super(message);
    }

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public PersistenceException(Throwable cause) {
        super(cause);
    }
}
