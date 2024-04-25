package myPrivateShareCar.service;

import myPrivateShareCar.Jwt.JwtRequest;
import myPrivateShareCar.Jwt.JwtResponse;

public interface AuthenticationService {
    JwtResponse login(JwtRequest jwtRequest);

    JwtResponse getNewAccessToken(String refreshToken);
}
