package auca.ac.rw.exception;

public class MembershipAlreadyActiveException extends RuntimeException {
    public MembershipAlreadyActiveException(String message) {
        super(message);
    }
}
