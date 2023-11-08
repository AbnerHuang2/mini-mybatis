package org.skitii.ibatis.exceptions;

/**
 * @author skitii
 * @since 2023/11/08
 **/
public class IbatisException extends RuntimeException{
    public IbatisException() {
        super();
    }

    public IbatisException(String message) {
        super(message);
    }

    public IbatisException(String message, Throwable cause) {
        super(message, cause);
    }

    public IbatisException(Throwable cause) {
        super(cause);
    }
}
