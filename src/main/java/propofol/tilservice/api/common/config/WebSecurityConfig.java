package propofol.tilservice.api.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import propofol.tilservice.api.common.filter.PreFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final PreFilter preFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

        http.formLogin().disable()
                .httpBasic().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().cors();

        http.authorizeRequests()
                .antMatchers("/api/v1/boards/**").authenticated()
                .anyRequest().permitAll()
                .and()
                .addFilterBefore(preFilter, UsernamePasswordAuthenticationFilter.class);

        http.headers().frameOptions().disable(); // h2-console을 보기 위한 설정
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);

        config.addAllowedOriginPattern("*"); // 모든 IP 응답 허용
        config.addAllowedHeader("*"); // 모든 헤더에 응답을 허용
        config.addAllowedMethod("*"); // 모든 post, get, delete, fetch 허용
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
