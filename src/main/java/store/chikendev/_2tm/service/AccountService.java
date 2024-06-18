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
import store.chikendev._2tm.entity.Role;
import store.chikendev._2tm.entity.RoleAccount;
import store.chikendev._2tm.entity.StateAccount;
import store.chikendev._2tm.exception.AppException;
import store.chikendev._2tm.exception.ErrorCode;
import store.chikendev._2tm.repository.AccountRepository;
import store.chikendev._2tm.repository.RoleAccountRepository;
import store.chikendev._2tm.repository.RoleRepository;
import store.chikendev._2tm.repository.StateAccountRepository;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private StateAccountRepository stateAccountRepository;

    @Autowired
    private RoleAccountRepository roleAccountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AccountResponse register(AccountRequest request) {
        Optional<Account> email = accountRepository.findByEmail(request.getEmail());
        Optional<Account> phone = accountRepository.findByPhoneNumber(request.getPhoneNumber());
        if (email.isPresent() || phone.isPresent()) {
            throw new AppException(ErrorCode.EMAIL_PHONE_EXISTED);
        }
        Optional<Account> username = accountRepository.findByUsername(request.getUsername());
        if (username.isPresent()) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        Role role = roleRepository.findById(Role.ROLE_CUSTOMER).get();
        StateAccount state = stateAccountRepository.findById(StateAccount.VERIFICATION_REQUIRED).get();
        Account account = mapper.map(request, Account.class);
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setViolationPoints(100);
        account.setState(state);

        Account save = accountRepository.save(account);

        RoleAccount roleAccount = RoleAccount.builder()
                .account(save)
                .role(role)
                .build();
        roleAccountRepository.save(roleAccount);
        AccountResponse response = mapper.map(account, AccountResponse.class);
        response.setStateName(save.getState().getName());
        return response;
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
