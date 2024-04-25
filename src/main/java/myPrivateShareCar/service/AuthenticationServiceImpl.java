package myPrivateShareCar.service;

import lombok.AllArgsConstructor;
import myPrivateShareCar.Jwt.JwtRequest;
import myPrivateShareCar.Jwt.JwtResponse;
import myPrivateShareCar.Jwt.JwtTokenUtils;
import myPrivateShareCar.exception.LoginOrPasswordException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtTokenUtils jwtTokenUtils;

    @Override
    public JwtResponse login(JwtRequest jwtRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            jwtRequest.getUserName(), jwtRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new LoginOrPasswordException("Неправильный логин или пароль");
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(jwtRequest.getUserName());
        String accessToken = jwtTokenUtils.generateAccessToken(userDetails);
        String refreshToken = jwtTokenUtils.generateRefreshToken(userDetails);
        return new JwtResponse(accessToken, refreshToken);
    }

    @Override
    public JwtResponse getNewAccessToken(String refreshToken) {
        String refreshUserName = jwtTokenUtils.getUserNameFromRefreshToken(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(refreshUserName);
        String accessToken = jwtTokenUtils.generateAccessToken(userDetails);
        return new JwtResponse(accessToken, null);
    }
}
