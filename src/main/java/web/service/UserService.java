package web.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import web.repository.user.AtenRepository;
import web.repository.user.FrenRepository;
import web.repository.user.SetRepository;
import web.repository.user.UsersRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    // [*] DI
    private final UsersRepository usersRepository;
    private final FrenRepository frenRepository;
    private final AtenRepository atenRepository;
    private final SetRepository setRepository;


}
