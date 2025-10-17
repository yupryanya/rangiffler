package guru.qa.rangiffler.model;

import guru.qa.rangiffler.defs.Country;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class PhotoCardJson {
  String photo;
  int likes;
  Country country;
  String description;
}