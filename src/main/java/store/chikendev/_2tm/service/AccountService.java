package store.chikendev._2tm.service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import store.chikendev._2tm.utils.SendEmail;

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

    @Autowired
    private SendEmail sendEmail;

    private final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private final int PASSWORD_LENGTH = 6;

    public AccountResponse register(AccountRequest request) {
        Optional<Account> email = accountRepository.findByEmail(request.getEmail());
        if (email.isPresent()) {
            if (email.get().getState().getName().equals("Verification Required")) {
                accountRepository.delete(email.get());
            } else {
                throw new AppException(ErrorCode.EMAIL_PHONE_EXISTED);
            }
        }
        Optional<Account> phone = accountRepository.findByPhoneNumber(request.getPhoneNumber());
        if (phone.isPresent()) {
            if (phone.get().getState().getName().equals("Verification Required")) {
                accountRepository.delete(phone.get());
            } else {
                throw new AppException(ErrorCode.EMAIL_PHONE_EXISTED);
            }
        }
        Optional<Account> username = accountRepository.findByUsername(request.getUsername());
        if (username.isPresent()) {
            throw new AppException(ErrorCode.EMAIL_PHONE_EXISTED);
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
        if (email.isPresent()) {
            throw new AppException(ErrorCode.EMAIL_PHONE_EXISTED);
        }
        Optional<Account> phone = accountRepository.findByPhoneNumber(request.getPhoneNumber());
        if (phone.isPresent()) {
            throw new AppException(ErrorCode.EMAIL_PHONE_EXISTED);
        }
        Optional<Account> username = accountRepository.findByUsername(request.getUsername());
        if (username.isPresent()) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        StateAccount state = stateAccountRepository.findById(StateAccount.ACTIVE)
                .orElseThrow(() -> new AppException(ErrorCode.STATE_NOT_FOUND));
        String password = generateRandomPassword();
        Account account = mapper.map(request, Account.class);
        account.setPassword(passwordEncoder.encode(password));
        account.setViolationPoints(100);
        account.setState(state);
        Account savedAccount = accountRepository.save(account);

        List<RoleAccount> roles = new ArrayList<>();
        for (String roleId : request.getRoles()) {
            Role role = roleRepository.findById(roleId).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
            RoleAccount roleAccount = RoleAccount.builder()
                    .account(savedAccount)
                    .role(role)
                    .build();
            roles.add(roleAccount);
        }

        CreateStaffResponse response = mapper.map(savedAccount, CreateStaffResponse.class);
        response.setStateName(savedAccount.getState().getName());
        response.setRoles(roleAccountRepository.saveAll(roles).stream()
                .map(roleAccount -> mapper.map(roleAccount.getRole(), RoleResponse.class)).toList());

        if (request.getStoreId() != null) {
            System.out.println(request.getStoreId());
            Store store = storeRepository.findById(request.getStoreId())
                    .orElseThrow(() -> new AppException(ErrorCode.STORE_NOT_FOUND));

            AccountStore accountStore = AccountStore.builder()
                    .account(savedAccount)
                    .store(store)
                    .build();
            accountStoreRepository.save(accountStore);
            response.setNameStore(store.getName());
        }
        String subject = "2TM PHÂN CÔNG";
        String content = "";
        if (response.getNameStore() != null) {
            content = "Chào mừng bạn đến với hệ thống 2TM. " + "Bạn đã được phân công vào cửa hàng: "
                    + response.getNameStore()
                    + " \nTài khoản: " + response.getUsername() + "\nMật khẩu: " + password;
        } else {
            content = "Chào mừng bạn đến với hệ thống 2TM. " + "Bạn đã được phân công vào hệ thống 2TM"
                    + " \nTài khoản: " + response.getUsername() + "\nMật khẩu: " + password;
        }
        sendEmail.sendMail(request.getEmail(), subject, content);
        return response;
    }

    public Page<CreateStaffResponse> getAllStaff(Optional<Integer> pageNo) {
        Pageable pageable = PageRequest.of(pageNo.orElse(0), 10);
        List<String> excludedRoles = Arrays.asList("CH", "KH", "ND");
        Page<Account> roles = roleAccountRepository.findByRoleStaff(excludedRoles, pageable);
        return roles.map(account -> {
            CreateStaffResponse response = mapper.map(account, CreateStaffResponse.class);
            response.setStateName(account.getState().getName());
            response.setRoles(roleAccountRepository.findByAccount(account).stream()
                    .map(roleAccount -> mapper.map(roleAccount.getRole(), RoleResponse.class)).toList());
            return response;
        });
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
