package store.chikendev._2tm.service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import store.chikendev._2tm.dto.request.AccountRequest;
import store.chikendev._2tm.dto.request.CreateStaffRequest;
import store.chikendev._2tm.dto.responce.AccountResponse;
import store.chikendev._2tm.dto.responce.CreateStaffResponse;
import store.chikendev._2tm.dto.responce.RoleResponse;
import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.entity.AccountStore;
import store.chikendev._2tm.entity.Role;
import store.chikendev._2tm.entity.RoleAccount;
import store.chikendev._2tm.entity.StateAccount;
import store.chikendev._2tm.entity.Store;
import store.chikendev._2tm.exception.AppException;
import store.chikendev._2tm.exception.ErrorCode;
import store.chikendev._2tm.repository.AccountRepository;
import store.chikendev._2tm.repository.AccountStoreRepository;
import store.chikendev._2tm.repository.RoleAccountRepository;
import store.chikendev._2tm.repository.RoleRepository;
import store.chikendev._2tm.repository.StateAccountRepository;
import store.chikendev._2tm.repository.StoreRepository;

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
    private StoreRepository storeRepository;

    @Autowired
    private AccountStoreRepository accountStoreRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private final int PASSWORD_LENGTH = 6;

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
        StateAccount state = stateAccountRepository.findById(StateAccount.VERIFICATION_REQUIRED).get();
        Account account = mapper.map(request, Account.class);
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setViolationPoints(100);
        account.setState(state);
        Account save = accountRepository.save(account);
        Role role = roleRepository.findById(Role.ROLE_CUSTOMER).get();
        RoleAccount roleAccount = RoleAccount.builder()
                .account(save)
                .role(role)
                .build();
        roleAccountRepository.save(roleAccount);
        AccountResponse response = mapper.map(account, AccountResponse.class);
        response.setStateName(save.getState().getName());
        return response;
    }

    public CreateStaffResponse createStaff(CreateStaffRequest request) {
        Optional<Account> email = accountRepository.findByEmail(request.getEmail());
        Optional<Account> phone = accountRepository.findByPhoneNumber(request.getPhoneNumber());
        if (email.isPresent() || phone.isPresent()) {
            throw new AppException(ErrorCode.EMAIL_PHONE_EXISTED);
        }
        Optional<Account> username = accountRepository.findByUsername(request.getUsername());
        if (username.isPresent()) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        StateAccount state = stateAccountRepository.findById(StateAccount.ACTIVE).get();
        String password = generateRandomPassword();
        Account account = mapper.map(request, Account.class);
        account.setPassword(passwordEncoder.encode(password));
        account.setViolationPoints(100);
        account.setState(state);
        Account save = accountRepository.save(account);

        List<RoleAccount> roles = new ArrayList<>();
        for (String roleId : request.getRoles()) {
            Role role = roleRepository.findById(roleId).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
            RoleAccount roleAccount = RoleAccount.builder()
                    .account(save)
                    .role(role)
                    .build();
            roles.add(roleAccount);
        }
        AccountStore accountStore = null;
        if (request.getIdStore() != null) {
            Store store = storeRepository.findById(request.getIdStore())
                    .orElseThrow(() -> new AppException(ErrorCode.STORE_NOT_FOUND));
            accountStore = accountStoreRepository.save(AccountStore.builder()
                    .account(save)
                    .store(store)
                    .build());
        }
        System.out.println(password);
        // fixx
        // sendEmail(request.getEmail(), password);
        CreateStaffResponse response = mapper.map(account, CreateStaffResponse.class);
        response.setStateName(save.getState().getName());
        response.setRoles(roleAccountRepository.saveAll(roles).stream()
                .map(roleAccount -> mapper.map(roleAccount.getRole(), RoleResponse.class)).toList());
        response.setNameStore(accountStore != null ? accountStore.getStore().getName() : null);
        return response;
    }

    // random pass
    public String generateRandomPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }
        return password.toString();
    }

}
