package myPrivateShareCar.repository;

import myPrivateShareCar.dto.FindCarDto;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class FindCarRepositoryImpl implements FindCarRepository {
    private final List<FindCarDto> requestForCar = new ArrayList<>();

    @Override
    public void findCar(FindCarDto findCarDto) {
        requestForCar.add(findCarDto);
    }

    @Override
    public List<FindCarDto> allRequestCar() {
        return requestForCar;
    }
}
