package br.com.alura.bytebank.domain.exceptions;

public class NotUpdateException extends RuntimeException{

    public NotUpdateException(String message) {
        super(message);
    }
}
