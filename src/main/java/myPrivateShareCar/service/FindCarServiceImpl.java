package myPrivateShareCar.service;

import lombok.RequiredArgsConstructor;
import myPrivateShareCar.dto.FindCarDto;
import myPrivateShareCar.repository.FindCarRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FindCarServiceImpl implements FindCarService {
    private final FindCarRepository findCarRepository;

    @Override
    public void findCar(FindCarDto findCarDto) {
        findCarRepository.findCar(findCarDto);
    }

    @Override
    public List<FindCarDto> allRequestCar() {
        return findCarRepository.allRequestCar();
    }
}
