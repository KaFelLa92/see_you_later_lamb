package web.service;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import web.repository.promise.*;

@Service
@Transactional
@RequiredArgsConstructor
public class PromService {
    // [*] DI
    private final PromRepository promRepository;
    private final ShareRepository shareRepository;
    private final CalendRepository calendRepository;
    private final EvalRepository evalRepository;
    private final TempRepository tempRepository;


}
