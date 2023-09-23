package myPrivateShareCar.service;

import myPrivateShareCar.model.RequestCar;

import java.util.List;

public interface RequestCarService {
    void requestCar(RequestCar requestCar);
    List<RequestCar> allRequestCar();
}
