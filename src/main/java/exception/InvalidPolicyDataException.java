package exception;

public class InvalidPolicyDataException extends IllegalArgumentException{
    public InvalidPolicyDataException(String s) {
        super(s);
    }
}
