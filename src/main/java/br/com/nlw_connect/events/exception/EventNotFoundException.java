package br.com.nlw_connect.events.exception;

public class EventNotFoundException extends RuntimeException{

    public EventNotFoundException(String msg) {
        super(msg);
    }
}
