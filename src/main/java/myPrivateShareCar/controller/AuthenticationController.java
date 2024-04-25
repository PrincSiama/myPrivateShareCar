package myPrivateShareCar.controller;

import lombok.RequiredArgsConstructor;
import myPrivateShareCar.Jwt.JwtRequest;
import myPrivateShareCar.Jwt.JwtResponse;
import myPrivateShareCar.dto.RefreshTokenDto;
import myPrivateShareCar.service.AuthenticationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public JwtResponse login(@RequestBody JwtRequest jwtRequest) {
        return authenticationService.login(jwtRequest);
    }

    @PostMapping("/token")
    public JwtResponse getNewAccessToken(@RequestBody RefreshTokenDto request) {
        return authenticationService.getNewAccessToken(request.getRefreshToken());
    }
}
