package guru.qa.rangiffler.jupiter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface User {
  String username() default "";

  Photo[] photos() default {};

  int randomPhoto() default 0;

  int friends() default 0;

  int incomingRequests() default 0;

  int outgoingRequests() default 0;
}