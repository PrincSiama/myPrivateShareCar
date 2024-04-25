package myPrivateShareCar.service;

import lombok.AllArgsConstructor;
import myPrivateShareCar.exception.NotFoundException;
import myPrivateShareCar.model.User;
import myPrivateShareCar.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@AllArgsConstructor
public class UserPrincipalServiceImpl implements UserPrincipalService {
    private final UserRepository userRepository;
    @Override
    public User getUserFromPrincipal(Principal principal) {
        return userRepository.findByEmail(principal.getName()).orElseThrow(
                () -> new NotFoundException("Пользователь с email " + principal.getName() + " не найден"));
    }
}
