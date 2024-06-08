package store.chikendev._2tm.service;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import store.chikendev._2tm.dto.request.AccountRequest;
import store.chikendev._2tm.dto.responce.AccountResponse;
import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.exception.AppException;
import store.chikendev._2tm.exception.ErrorCode;
import store.chikendev._2tm.repository.AccountRepository;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AccountResponse register(AccountRequest request) {
        Optional<Account> validate = accountRepository.findByEmail(request.getEmail());
        if (validate.isPresent()) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        Account account = mapper.map(request, Account.class);
        account.setPassword(passwordEncoder.encode(request.getPassword()));

        return mapper.map(accountRepository.save(account), AccountResponse.class);
    }

    public List<AccountResponse> getAll() {
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream().map(account -> mapper.map(account, AccountResponse.class)).toList();
    }

    public AccountResponse getOne(String id) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return mapper.map(account, AccountResponse.class);
    }

}
