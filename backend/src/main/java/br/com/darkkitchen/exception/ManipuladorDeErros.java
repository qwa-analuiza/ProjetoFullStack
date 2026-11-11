package br.com.darkkitchen.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ManipuladorDeErros {

    public record ErroResposta(Instant data, int status, String mensagem, Map<String, String> campos) {}

    private ResponseEntity<ErroResposta> erro(HttpStatus status, String mensagem, Map<String, String> campos) {
        return ResponseEntity.status(status).body(new ErroResposta(Instant.now(), status.value(), mensagem, campos));
    }

    @ExceptionHandler(NaoEncontradoException.class)
    public ResponseEntity<ErroResposta> naoEncontrado(NaoEncontradoException e) {
        return erro(HttpStatus.NOT_FOUND, e.getMessage(), null);
    }

    @ExceptionHandler(NaoAutorizadoException.class)
    public ResponseEntity<ErroResposta> naoAutorizado(NaoAutorizadoException e) {
        return erro(HttpStatus.UNAUTHORIZED, e.getMessage(), null);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResposta> regraNegocio(RegraNegocioException e) {
        return erro(HttpStatus.BAD_REQUEST, e.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResposta> validacao(MethodArgumentNotValidException e) {
        Map<String, String> campos = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors().forEach(f -> campos.putIfAbsent(f.getField(), f.getDefaultMessage()));
        return erro(HttpStatus.BAD_REQUEST, "Dados inválidos", campos);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErroResposta> requisicaoInvalida(Exception e) {
        return erro(HttpStatus.BAD_REQUEST, "Requisição malformada", null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResposta> conflito(DataIntegrityViolationException e) {
        log.warn("Violação de integridade: {}", e.getMostSpecificCause().getMessage());
        return erro(HttpStatus.CONFLICT, "Registro duplicado ou em uso por outros dados", null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResposta> inesperado(Exception e) {
        if (e instanceof ErrorResponse padrao) // 404, 405, 415... já têm status próprio
            return erro(HttpStatus.valueOf(padrao.getStatusCode().value()), "Requisição inválida", null);
        log.error("Erro inesperado", e);
        return erro(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor", null);
    }
}
