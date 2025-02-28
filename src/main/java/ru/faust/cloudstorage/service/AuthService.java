package ru.faust.cloudstorage.service;

import ru.faust.cloudstorage.dto.UserRegistrationDTO;

public interface AuthService {

    void register(UserRegistrationDTO userRegistrationDTO);

}
