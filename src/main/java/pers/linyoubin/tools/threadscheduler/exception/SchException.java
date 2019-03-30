package pers.linyoubin.tools.threadscheduler.exception;

public class SchException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = 3668407999322518682L;

    public SchException(String msg) {
        super(msg);
    }

    public SchException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public SchException(Throwable cause) {
        super(cause);
    }
}
