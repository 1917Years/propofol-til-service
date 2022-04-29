package propofol.tilservice.api.common.exception.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import propofol.tilservice.api.common.exception.NotMatchMemberException;
import propofol.tilservice.api.common.exception.SameMemberException;
import propofol.tilservice.api.common.exception.dto.ErrorDetailDto;
import propofol.tilservice.api.common.exception.dto.ErrorDto;
import propofol.tilservice.domain.exception.NotFoundBoardException;
import propofol.tilservice.domain.exception.NotFoundCommentException;
import propofol.tilservice.domain.exception.NotFoundFileException;
import propofol.tilservice.domain.exception.NotSaveFileException;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
@Slf4j
public class ExceptionAdviceController {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto SQLException(ConstraintViolationException e){
        log.info("Message = {}", e.getMessage());
        return null;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto NotFoundBoardException(NotFoundBoardException e){
        ErrorDto errorDto = createError(e.getMessage(), HttpStatus.BAD_REQUEST);
        return errorDto;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto NotMatchMemberException(NotMatchMemberException e){
        ErrorDto errorDto = createError(e.getMessage(), HttpStatus.BAD_REQUEST);
        return errorDto;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto NotSameMemberException(SameMemberException e){
        ErrorDto errorDto = createError(e.getMessage(), HttpStatus.BAD_REQUEST);
        return errorDto;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto NotSaveFileException(NotSaveFileException e){
        ErrorDto errorDto = createError(e.getMessage(), HttpStatus.BAD_REQUEST);
        return errorDto;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto NotFoundFileException(NotFoundFileException e){
        ErrorDto errorDto = createError(e.getMessage(), HttpStatus.BAD_REQUEST);
        return errorDto;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto NotFoundCommentException(NotFoundCommentException e){
        ErrorDto errorDto = createError(e.getMessage(), HttpStatus.BAD_REQUEST);
        return errorDto;
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto badRequestType1Error(HttpMessageNotReadableException e){
        ErrorDto errorDto = createError("잘못된 요청입니다.", HttpStatus.BAD_REQUEST);
        return errorDto;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto validationError(MethodArgumentNotValidException e){
        ErrorDto errorDto = createError("게시글 생성 실패!", HttpStatus.BAD_REQUEST);
        e.getFieldErrors().forEach(
                error -> {
                    errorDto.getErrors().add(new ErrorDetailDto(error.getField(), error.getDefaultMessage()));
                }
        );
        return errorDto;
    }

    private ErrorDto createError(String errorMessage, HttpStatus httpStatus) {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setMessage(errorMessage);
        errorDto.setStatus(httpStatus.value());
        return errorDto;
    }
}
