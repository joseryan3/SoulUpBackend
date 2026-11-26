package br.com.senai.SoulUp.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
// CORREÇÃO AQUI: Importar do java.lang.annotation
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Constraint(validatedBy = PasswordValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD }) // Agora o Java vai reconhecer isso
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String message() default "A senha deve ter no mínimo 8 caracteres, incluindo letra maiúscula, número e caractere especial.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}