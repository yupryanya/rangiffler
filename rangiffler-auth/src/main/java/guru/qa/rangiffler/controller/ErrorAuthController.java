package guru.qa.rangiffler.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorAuthController implements ErrorController {

  private static final String ERROR_VIEW_NAME = "error";

  private final String rangifflerFrontUri;

  public ErrorAuthController(@Value("${rangiffler-front.base-uri}") String rangifflerFrontUri) {
    this.rangifflerFrontUri = rangifflerFrontUri;
  }

  @GetMapping("/error")
  public String error(HttpServletRequest request, HttpServletResponse response, Model model) {
    int status = response.getStatus();
    Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
    model.addAttribute("status", status);
    model.addAttribute("frontUri", rangifflerFrontUri + "/my-travels");
    model.addAttribute("error", throwable != null ? throwable.getMessage() : "Unknown error");
    return ERROR_VIEW_NAME;
  }
}
