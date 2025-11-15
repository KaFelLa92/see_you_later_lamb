package web.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.repository.UsersRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    // [*] DI
    private final UsersRepository usersRepository;

}
