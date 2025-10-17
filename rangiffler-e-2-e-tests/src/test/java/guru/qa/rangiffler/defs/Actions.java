package guru.qa.rangiffler.defs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Actions {
  ADD("ADD", "Add"),
  ACCEPT("ACCEPT", "Accept"),
  DECLINE("DECLINE", "Decline"),
  REMOVE("REMOVE", "Remove");

  private final String action;
  private final String buttonText;
}
