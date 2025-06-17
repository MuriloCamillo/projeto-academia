package br.com.gymtime.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manipulador de exceções global para toda a aplicação.
 * A anotação @ControllerAdvice permite que esta classe intercepte e processe
 * exceções lançadas por qualquer controller, centralizando o tratamento de erros.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * DTO para padronizar o corpo da resposta de erros genéricos.
     */
    private record ErrorDetails(LocalDateTime timestamp, int status, String error, String message, String path) {}

    /**
     * DTO para padronizar o corpo da resposta de erros de validação.
     */
    private record ValidationErrorDetails(LocalDateTime timestamp, int status, String error, List<String> messages, String path) {}


    /**
     * Manipula a exceção {@link ResourceNotFoundException}.
     * Retorna um status HTTP 404 (Not Found).
     * @param ex A exceção lançada.
     * @param request O contexto da requisição web.
     * @return Um ResponseEntity contendo os detalhes do erro e o status 404.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Recurso Não Encontrado",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    /**
     * Manipula a exceção {@link EmailAlreadyExistsException}.
     * Retorna um status HTTP 409 (Conflict).
     * @param ex A exceção lançada.
     * @param request O contexto da requisição web.
     * @return Um ResponseEntity contendo os detalhes do erro e o status 409.
     */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorDetails> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "Conflito de Dados - Email",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    /**
     * Manipula a exceção {@link CpfAlreadyExistsException}.
     * Retorna um status HTTP 409 (Conflict).
     * @param ex A exceção lançada.
     * @param request O contexto da requisição web.
     * @return Um ResponseEntity contendo os detalhes do erro e o status 409.
     */
    @ExceptionHandler(CpfAlreadyExistsException.class)
    public ResponseEntity<ErrorDetails> handleCpfAlreadyExistsException(CpfAlreadyExistsException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "Conflito de Dados - CPF",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    /**
     * Manipula a exceção {@link MethodArgumentNotValidException}, lançada quando a validação de um
     * argumento anotado com @Valid falha.
     * Retorna um status HTTP 400 (Bad Request).
     * @param ex A exceção de validação.
     * @param headers Os headers da resposta.
     * @param status O status HTTP.
     * @param request O contexto da requisição.
     * @return Um ResponseEntity com uma lista detalhada dos erros de validação.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {

        List<String> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        ValidationErrorDetails validationErrorDetails = new ValidationErrorDetails(
                LocalDateTime.now(),
                status.value(),
                "Erro de Validação",
                validationErrors,
                request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(validationErrorDetails, headers, status);
    }

    /**
     * Manipulador "catch-all" para qualquer outra exceção não tratada especificamente.
     * Loga a exceção completa no servidor para depuração e retorna uma mensagem de erro
     * genérica e segura para o cliente.
     * Retorna um status HTTP 500 (Internal Server Error).
     * @param ex A exceção inesperada.
     * @param request O contexto da requisição.
     * @return Um ResponseEntity com uma mensagem de erro genérica e o status 500.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(Exception ex, WebRequest request) {
        // Loga a exceção completa no servidor para fins de depuração. É crucial para a manutenção.
        log.error("ERRO INESPERADO NÃO TRATADO: ", ex);

        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro Interno do Servidor",
                "Ocorreu um erro inesperado. Por favor, tente novamente mais tarde ou contate o suporte.",
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
