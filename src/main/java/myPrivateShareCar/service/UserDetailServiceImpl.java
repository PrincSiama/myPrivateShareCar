package myPrivateShareCar.service;

import myPrivateShareCar.config.UserDetailsImpl;
import myPrivateShareCar.exception.NotFoundException;
import myPrivateShareCar.model.User;
import myPrivateShareCar.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public class UserDetailServiceImpl implements UserDetailsService {
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(username);
        return user.map(UserDetailsImpl::new)
                .orElseThrow(() -> new NotFoundException("Пользователь с email " + username + " не найден"));
    }
}
