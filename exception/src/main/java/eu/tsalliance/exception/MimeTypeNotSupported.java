package eu.tsalliance.exception;

import org.springframework.http.HttpStatus;
import java.util.List;

public class MimeTypeNotSupported extends ApiException {


    public MimeTypeNotSupported(String mimeType, List<String> supported) {
        super("MIME-Type of file not supported", HttpStatus.BAD_REQUEST);
        this.putDetail("found", mimeType);
        this.putDetail("expectedOf", supported);
    }

    @Override
    protected String getErrorCode() {
        return "UNSUPPORTED_MIMETYPE";
    }
}
