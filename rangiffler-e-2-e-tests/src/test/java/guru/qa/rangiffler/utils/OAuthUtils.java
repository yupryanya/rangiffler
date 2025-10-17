package guru.qa.rangiffler.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class OAuthUtils {
  public static String generateCodeVerifier() {
    byte[] code = new byte[32];
    new SecureRandom().nextBytes(code);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(code);
  }

  public static String generateCodeChallenge(String codeVerifier) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
      return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
    } catch (Exception e) {
      throw new RuntimeException("Could not generate code challenge", e);
    }
  }
}
