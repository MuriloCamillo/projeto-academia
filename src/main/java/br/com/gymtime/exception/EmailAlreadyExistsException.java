package br.com.gymtime.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção lançada quando há uma tentativa de cadastrar um aluno com um e-mail
 * que já existe no banco de dados.
 * A anotação {@code @ResponseStatus(HttpStatus.CONFLICT)} instrui o Spring a
 * retornar o status HTTP 409 (Conflict), que é o mais apropriado para indicar
 * que a requisição não pôde ser concluída devido a uma duplicação de dados.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class EmailAlreadyExistsException extends RuntimeException {

    /**
     * Construtor da exceção.
     *
     * @param message A mensagem de erro detalhando o conflito.
     */
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
