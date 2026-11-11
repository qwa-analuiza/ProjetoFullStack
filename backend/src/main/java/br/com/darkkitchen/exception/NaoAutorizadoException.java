package br.com.darkkitchen.exception;

public class NaoAutorizadoException extends RuntimeException {
    public NaoAutorizadoException(String mensagem) { super(mensagem); }
}
