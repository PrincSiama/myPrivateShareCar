package myPrivateShareCar.config;

import myPrivateShareCar.service.UserDetailServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailServiceImpl();
    }

    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
//                 .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // todo реквестМатчер не принимает строку. Применён new AntPathRequestMatcher
                        .anyRequest().permitAll())
                .build();
    }


    /*.requestMatchers(new AntPathRequestMatcher("/users/create")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/cars/search")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/users/**")).authenticated()
                        .requestMatchers(new AntPathRequestMatcher("/cars/**")).authenticated()
                        .requestMatchers(new AntPathRequestMatcher("/booking/**")).authenticated())
                .formLogin(AbstractAuthenticationFilterConfigurer::permitAll)
                .build();*/

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

/*http.cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests().antMatchers("/api/auth/**").permitAll()
                .antMatchers("/api/test/**").permitAll()
                .anyRequest().authenticated();*/

}
