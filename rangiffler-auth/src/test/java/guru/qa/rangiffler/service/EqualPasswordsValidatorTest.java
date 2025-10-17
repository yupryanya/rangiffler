package guru.qa.rangiffler.service;

import guru.qa.rangiffler.model.RegistrationModel;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EqualPasswordsValidatorTest {

  private final EqualPasswordsValidator equalPasswordsValidator = new EqualPasswordsValidator();

  @Test
  void isValidTest(@Mock ConstraintValidatorContext context) {
    RegistrationModel rm = new RegistrationModel(
        "test",
        "qwerty",
        "qwerty"
    );

    assertThat(equalPasswordsValidator.isValid(rm, context)).isTrue();
  }

  @Test
  void isValidNegativeTest(
      @Mock ConstraintValidatorContext context,
      @Mock ConstraintValidatorContext.ConstraintViolationBuilder constraintViolationBuilder,
      @Mock ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilderCustomizableContext) {
    when(context.buildConstraintViolationWithTemplate(any()))
        .thenReturn(constraintViolationBuilder);

    when(constraintViolationBuilder.addPropertyNode(eq("password")))
        .thenReturn(nodeBuilderCustomizableContext);

    RegistrationModel rm = new RegistrationModel(
        "test",
        "qwerty",
        "qwerty12345"
    );

    assertThat(equalPasswordsValidator.isValid(rm, context)).isFalse();
  }
}