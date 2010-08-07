package org.dodgybits.shuffle.android.synchronisation.tracks;

@SuppressWarnings("serial")
public class ApiException extends Exception {

    public ApiException(String reason) {
        super(reason);
    }
    
    public ApiException(String reason, Exception e) {
        super(reason, e);
    }

}
