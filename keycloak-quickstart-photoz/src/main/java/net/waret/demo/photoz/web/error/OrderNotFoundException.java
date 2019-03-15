package net.waret.demo.photoz.web.error;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(Long id) {
        super("Could not find order " + id);
    }

}
