package myPrivateShareCar.service;

import lombok.RequiredArgsConstructor;
import myPrivateShareCar.model.RequestCar;
import myPrivateShareCar.repository.JpaRequestCarRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestCarServiceImpl implements RequestCarService {
    private final JpaRequestCarRepository requestCarRepository;

    @Override
    public void requestCar(RequestCar requestCar) {
        requestCarRepository.save(requestCar);
    }

    @Override
    public List<RequestCar> allRequestCar() {
        return requestCarRepository.findAll(PageRequest.of(0, 5)).stream().collect(Collectors.toList());
    }
}
