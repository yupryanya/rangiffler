package guru.qa.rangiffler.jupiter.annotation;

import guru.qa.rangiffler.defs.Country;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Photo {
  String src() default "img/default-photo.png";

  int likes() default 0;

  Country country() default Country.BY;

  String description() default "Description";
}