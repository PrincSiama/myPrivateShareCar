package myPrivateShareCar.repository;

import myPrivateShareCar.dto.FindCarDto;

import java.util.List;

public interface FindCarRepository {
    void findCar(FindCarDto findCarDto);
    List<FindCarDto> allRequestCar();
}
