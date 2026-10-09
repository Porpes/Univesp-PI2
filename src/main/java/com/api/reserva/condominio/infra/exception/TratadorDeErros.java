package com.api.reserva.condominio.infra.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class TratadorDeErros {

    // Erro 404 - Quando o Hibernate não acha a entidade
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> tratarErro404() {
        return ResponseEntity.notFound().build();
    }

    // Erro 404 - Quando o Java não acha o elemento na lista
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> tratarErroElementoNaoEncontrado() {
        return ResponseEntity.notFound().build();
    }

    // Erro 400 - Quando o tipo de dado enviado na URL está errado (ex: passar texto onde espera ID)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> tratarErroDeArgumento(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.badRequest().body("Argumento incorreto ou mal formado.");
    }

    // Erro 400 - JSON inválido (faltando vírgula, chave, etc)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> tratarErro400(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    // Erro 400 - Falha de validação do Bean Validation (@NotNull, @NotBlank, etc)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> tratarErroValidacao(MethodArgumentNotValidException ex) {
        var erros = ex.getFieldErrors();
        return ResponseEntity.badRequest().body(erros.stream().map(DadosErroValidacao::new).toList());
    }

    // // Erro 401 - Login ou Senha inválidos
    // @ExceptionHandler(BadCredentialsException.class)
    // public ResponseEntity tratarErro401() {
    //     return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas.");
    // }

    // Erro 500 - O "Pega Tudo" para a aplicação não explodir no Render
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> tratarErro500(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno: " + ex.getLocalizedMessage());
    }
}