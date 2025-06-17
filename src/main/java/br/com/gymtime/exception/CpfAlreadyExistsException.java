package br.com.gymtime.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção lançada quando há uma tentativa de cadastrar um aluno com um CPF
 * que já existe no banco de dados.
 * A anotação {@code @ResponseStatus(HttpStatus.CONFLICT)} instrui o Spring a
 * retornar o status HTTP 409 (Conflict) sempre que esta exceção não for
 * tratada por um manipulador mais específico.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class CpfAlreadyExistsException extends RuntimeException {

    /**
     * Construtor da exceção.
     *
     * @param message A mensagem de erro detalhando o conflito.
     */
    public CpfAlreadyExistsException(String message) {
        super(message);
    }
}
