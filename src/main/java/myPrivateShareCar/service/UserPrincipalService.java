package myPrivateShareCar.service;

import myPrivateShareCar.model.User;

import java.security.Principal;

public interface UserPrincipalService {
    User getUserFromPrincipal(Principal principal);
}
