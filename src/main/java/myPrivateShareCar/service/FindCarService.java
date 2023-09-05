package myPrivateShareCar.service;

import myPrivateShareCar.dto.FindCarDto;

import java.util.List;

public interface FindCarService {
    void findCar(FindCarDto findCarDto);
    List<FindCarDto> allRequestCar();
}
