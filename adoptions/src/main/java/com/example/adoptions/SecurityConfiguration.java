package com.example.adoptions;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authorization.EnableMultiFactorAuthentication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.FactorGrantedAuthority;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.sql.DataSource;
import java.security.Principal;
import java.util.Map;

@EnableMultiFactorAuthentication(authorities = {
        FactorGrantedAuthority.OTT_AUTHORITY,
        FactorGrantedAuthority.PASSWORD_AUTHORITY
})
@Configuration
class SecurityConfiguration {

    @Bean
    JdbcUserDetailsManager jdbcUserDetailsManager(DataSource d) {
        return new JdbcUserDetailsManager(d);
    }

    @Bean
    Customizer<HttpSecurity> httpSecurityCustomizer() {
        return http -> http
                .webAuthn(a -> a.rpId("localhost").rpName("bootiful").allowedOrigins("http://localhost:8080"))
                .oneTimeTokenLogin(httpSecurityOneTimeTokenLoginConfigurer -> httpSecurityOneTimeTokenLoginConfigurer.tokenGenerationSuccessHandler(
                        (request, response, oneTimeToken) -> {

                            response.getWriter().println("you've got console mail!");
                            response.setContentType(MediaType.TEXT_PLAIN_VALUE);

                            IO.println("please go to http://localhost:8080/login/ott?token=" + oneTimeToken.getTokenValue());
                        }
                ));
    }

}

@Controller
@ResponseBody
class PrincipalController {

    @GetMapping("/")
    Map<String, String> home(Principal principal) {
        return Map.of("user", principal.getName());
    }
}
