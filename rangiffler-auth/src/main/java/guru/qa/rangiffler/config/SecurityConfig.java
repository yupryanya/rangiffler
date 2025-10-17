package guru.qa.rangiffler.config;

import guru.qa.rangiffler.service.SpecificRequestDumperFilter;
import guru.qa.rangiffler.service.cors.CookieCsrfFilter;
import guru.qa.rangiffler.service.cors.CorsCustomizer;
import org.apache.catalina.filters.RequestDumperFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.session.DisableEncodeUrlFilter;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
public class SecurityConfig {

  private final CorsCustomizer corsCustomizer;
  private final Environment environment;

  @Autowired
  public SecurityConfig(CorsCustomizer corsCustomizer, Environment environment) {
    this.corsCustomizer = corsCustomizer;
    this.environment = environment;
  }

  @Bean
  public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
    corsCustomizer.corsCustomizer(http);

    if (environment.acceptsProfiles(Profiles.of("local", "staging"))) {
      http.addFilterBefore(new SpecificRequestDumperFilter(
          new RequestDumperFilter(),
          "/login", "/oauth2/.*"
      ), DisableEncodeUrlFilter.class);
    }

    return http.authorizeHttpRequests(customizer -> customizer
            .requestMatchers(
                antMatcher("/register"),
                antMatcher("/error"),
                antMatcher("/images/**"),
                antMatcher("/styles/**"),
                antMatcher("/fonts/**"),
                antMatcher("/.well-known/**"),
                antMatcher("/actuator/health")
            ).permitAll()
            .anyRequest()
            .authenticated()
        )
        .csrf(csrf -> csrf
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
        )
        .addFilterAfter(new CookieCsrfFilter(), BasicAuthenticationFilter.class)
        .formLogin(login -> login
            .loginPage("/login")
            .permitAll())
        .logout(logout -> logout
            .logoutRequestMatcher(antMatcher("/logout"))
            .deleteCookies("JSESSIONID", "XSRF-TOKEN")
            .invalidateHttpSession(true)
            .clearAuthentication(true)
            .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
        )
        .exceptionHandling(customizer -> customizer
            .accessDeniedPage("/error")
        )
        .sessionManagement(sm -> sm.invalidSessionUrl("/login"))
        .build();
  }
}