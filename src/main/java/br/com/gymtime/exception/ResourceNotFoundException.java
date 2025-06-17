package br.com.gymtime.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção lançada quando uma operação tenta acessar um recurso que não existe
 * no sistema (ex: buscar um aluno por um ID que não está cadastrado).
 * A anotação {@code @ResponseStatus(HttpStatus.NOT_FOUND)} instrui o Spring a
 * retornar o status HTTP 404 (Not Found) sempre que esta exceção não for
 * tratada por um manipulador mais específico.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Construtor da exceção.
     *
     * @param message A mensagem de erro detalhando qual recurso não foi encontrado.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
