package store.chikendev._2tm.service;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import store.chikendev._2tm.dto.request.AccountRequest;
import store.chikendev._2tm.dto.request.CreateStaffRequest;
import store.chikendev._2tm.dto.request.ChangePasswordRequest;
import store.chikendev._2tm.dto.responce.AccountResponse;
import store.chikendev._2tm.dto.responce.AddressResponse;
import store.chikendev._2tm.dto.responce.CreateStaffResponse;
import store.chikendev._2tm.dto.responce.ResponseDocumentDto;
import store.chikendev._2tm.dto.responce.RoleResponse;
import store.chikendev._2tm.dto.responce.StoreResponse;
import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.entity.AccountStore;
import store.chikendev._2tm.entity.Address;
import store.chikendev._2tm.entity.Image;
import store.chikendev._2tm.entity.Role;
import store.chikendev._2tm.entity.RoleAccount;
import store.chikendev._2tm.entity.StateAccount;
import store.chikendev._2tm.entity.Store;
import store.chikendev._2tm.entity.Ward;
import store.chikendev._2tm.exception.AppException;
import store.chikendev._2tm.exception.ErrorCode;
import store.chikendev._2tm.repository.AccountRepository;
import store.chikendev._2tm.repository.AccountStoreRepository;
import store.chikendev._2tm.repository.AddressRepository;
import store.chikendev._2tm.repository.ImageRepository;
import store.chikendev._2tm.repository.RoleAccountRepository;
import store.chikendev._2tm.repository.RoleRepository;
import store.chikendev._2tm.repository.StateAccountRepository;
import store.chikendev._2tm.repository.StoreRepository;
import store.chikendev._2tm.repository.WardRepository;
import store.chikendev._2tm.utils.EntityFileType;
import store.chikendev._2tm.utils.FilesHelp;
import store.chikendev._2tm.utils.SendEmail;
import store.chikendev._2tm.utils.dtoUtil.response.ImageDtoUtil;

@Service
public class AccountService {

    @Autowired
    private AddressService addressService;

    @Autowired
    private ImageRepository imageReponsitory;

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

    @Autowired
    private FilesHelp filesHelp;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private WardRepository wardRepository;

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
        Ward ward = wardRepository.findById(request.getIdWard()).orElseThrow(() -> {
            throw new AppException(ErrorCode.WARD_NOT_FOUND);
        });

        StateAccount state = stateAccountRepository.findById(StateAccount.ACTIVE)
                .orElseThrow(() -> new AppException(ErrorCode.STATE_NOT_FOUND));
        String password = generateRandomPassword();
        Account account = mapper.map(request, Account.class);
        account.setPassword(passwordEncoder.encode(password));
        account.setViolationPoints(100);
        account.setState(state);
        Account savedAccount = accountRepository.save(account);
        Address address = Address.builder()
                .account(savedAccount)
                .ward(ward)
                .streetAddress(request.getAddress())
                .build();
        savedAccount.setAddress(addressRepository.save(address));
        accountRepository.save(savedAccount);

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        RoleAccount roleAccount = RoleAccount.builder()
                .account(savedAccount)
                .role(role)
                .build();
        roleAccountRepository.save(roleAccount);
        CreateStaffResponse response = mapper.map(savedAccount, CreateStaffResponse.class);
        response.setStateName(savedAccount.getState().getName());
        response.setRoles(roleAccountRepository.findByAccount(savedAccount).stream()
                .map(roleStaff -> mapper.map(roleStaff.getRole(), RoleResponse.class)).toList());
        response.setAddress(getAddress(savedAccount.getAddress()));

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

    @SuppressWarnings("static-access")
    public Page<CreateStaffResponse> getAllStaff(Optional<Integer> pageNo) {
        Pageable pageable = PageRequest.of(pageNo.orElse(0), 10);
        List<String> excludedRoles = Arrays.asList("CH", "KH", "ND");
        Page<Account> roles = roleAccountRepository.findByRoleStaff(excludedRoles, pageable);
        return roles.map(account -> {
            CreateStaffResponse response = mapper.map(account, CreateStaffResponse.class);
            response.setStateName(account.getState().getName());
            response.setRoles(roleAccountRepository.findByAccount(account).stream()
                    .map(roleAccount -> mapper.map(roleAccount.getRole(), RoleResponse.class)).toList());
            List<ResponseDocumentDto> image = filesHelp.getDocuments(account.getId(), EntityFileType.USER_AVATAR);
            if (image.size() > 0) {
                response.setUrlImage(image.get(0));
            }
            return response;
        });
    }

    public boolean changePassword(ChangePasswordRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        boolean checkPass = passwordEncoder.matches(request.getPasswordOld(), account.getPassword());
        if (checkPass) {
            if (request.getPasswordNew().equals(request.getPasswordConfirm())) {
                account.setPassword(passwordEncoder.encode(request.getPasswordNew()));
                accountRepository.save(account);
                return true;
            } else {
                throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
            }
        }
        throw new AppException(ErrorCode.PASSWORD_NOT_FOUND);

    }

    public CreateStaffResponse updateStaff(String id, CreateStaffRequest request) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        account.setFullName(request.getFullName());
        account.setPhoneNumber(request.getPhoneNumber());
        account.setEmail(request.getEmail());
        account.setUsername(request.getUsername());
        Account savedAccount = accountRepository.save(account);

        if (request.getRoleId() != null) {
            List<RoleAccount> allRole = roleAccountRepository.findByAccount(savedAccount);
            allRole.forEach(role -> {
                if (role.getRole().getId().equals("NVCH") || role.getRole().getId().equals("NVGH")
                        || role.getRole().getId().equals("QLCH")) {
                    roleAccountRepository.delete(role);
                }
            });
        }

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        RoleAccount roleAccount = RoleAccount.builder()
                .account(savedAccount)
                .role(role)
                .build();
        roleAccountRepository.save(roleAccount);

        CreateStaffResponse response = mapper.map(savedAccount, CreateStaffResponse.class);
        response.setStateName(savedAccount.getState().getName());
        response.setRoles(roleAccountRepository.findByAccount(savedAccount).stream()
                .map(roleStaff -> mapper.map(roleStaff.getRole(), RoleResponse.class)).toList());

        if (request.getStoreId() != null) {
            Optional<AccountStore> storeOld = accountStoreRepository.findByAccount(savedAccount);
            if (storeOld.isPresent()) {
                accountStoreRepository.delete(storeOld.get());
            }

            Store store = storeRepository.findById(request.getStoreId())
                    .orElseThrow(() -> new AppException(ErrorCode.STORE_NOT_FOUND));

            AccountStore accountStore = AccountStore.builder()
                    .account(savedAccount)
                    .store(store)
                    .build();
            accountStoreRepository.save(accountStore);
            response.setNameStore(store.getName());
        }
        return response;
    }

    public String updateRoleNV(String idAccount, String idRole) {
        Account account = accountRepository.findById(idAccount)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Role role = roleRepository.findById(idRole)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        List<RoleAccount> allRole = roleAccountRepository.findByAccount(account);
        allRole.forEach(roleAccount -> {
            if (roleAccount.getRole().getId().equals("NVCH") || roleAccount.getRole().getId().equals("NVGH")
                    || roleAccount.getRole().getId().equals("QLCH")) {
                roleAccountRepository.delete(roleAccount);
            }
        });
        RoleAccount roleAccount = RoleAccount.builder()
                .account(account)
                .role(role)
                .build();
        roleAccountRepository.save(roleAccount);
        return "Cập nhật thành công";
    }

    public AccountResponse lockAccount(String id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        StateAccount lockedState = new StateAccount();
        lockedState.setId(StateAccount.LOCKED); // Set trạng thái là 5 (bị khóa)
        account.setState(lockedState);
        accountRepository.save(account);
        return mapper.map(account, AccountResponse.class);
    }

    @SuppressWarnings("static-access")
    public ResponseDocumentDto updateImage(String id, MultipartFile file) {
        Account account = accountRepository.findById(id).orElseThrow(() -> {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        });
        Image avatar = account.getImage();
        if (avatar != null) {
            filesHelp.deleteFile(account.getId(), avatar.getFileId(), EntityFileType.USER_AVATAR);
        } else {
            avatar = new Image();
        }
        var fileSaved = filesHelp.saveFile(file, account.getId(), EntityFileType.USER_AVATAR);

        avatar.setFileId(fileSaved.getFileId());
        avatar.setFileName(fileSaved.getFileName());
        avatar.setFileType(fileSaved.getFileType());
        avatar.setSize(fileSaved.getSize());
        avatar.setFileDownloadUri(fileSaved.getFileDownloadUri());

        imageReponsitory.save(avatar);

        return ImageDtoUtil.convertToImageResponse(avatar);
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

    public Account updateAccountById(String id, AccountRequest accountRequest) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Cập nhật thông tin tài khoản
        account.setUsername(accountRequest.getUsername());
        account.setPassword(accountRequest.getPassword());
        account.setFullName(accountRequest.getFullName());
        account.setPhoneNumber(accountRequest.getPhoneNumber());
        account.setEmail(accountRequest.getEmail());
        // Cập nhật các trường khác nếu cần thiết

        return accountRepository.save(account);
    }

    // Lấy thông tin Account
    public AccountResponse getAccountByToken() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        ResponseDocumentDto image = FilesHelp.getOneDocument(account.getId(), EntityFileType.USER_AVATAR);
        return AccountResponse.builder()
                .id(account.getId())
                .username(account.getUsername())
                .fullName(account.getFullName())
                .phoneNumber(account.getPhoneNumber())
                .email(account.getEmail())
                .roles(roleAccountRepository.findByAccount(account).stream()
                        .map(roleAccount -> mapper.map(roleAccount.getRole(), RoleResponse.class)).toList())
                .address(getAddress(account.getAddress()))
                .violationPoints(account.getViolationPoints())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .stateName(account.getState().getName())
                .image(image)
                .build();
    }

    public AccountResponse getStaffById(String id) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        ResponseDocumentDto image = FilesHelp.getOneDocument(account.getId(), EntityFileType.USER_AVATAR);

        AccountStore accountStore = accountStoreRepository.findByAccount(account).orElse(null);
        Store store = accountStore == null ? null : accountStore.getStore();

        return AccountResponse.builder()
                .id(account.getId())
                .username(account.getUsername())
                .fullName(account.getFullName())
                .phoneNumber(account.getPhoneNumber())
                .email(account.getEmail())
                .roles(roleAccountRepository.findByAccount(account).stream()
                        .map(roleAccount -> mapper.map(roleAccount.getRole(), RoleResponse.class)).toList())
                .address(getAddress(account.getAddress()))
                .violationPoints(account.getViolationPoints())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .stateName(account.getState().getName())
                .primaryAddress(
                        account.getAddress() != null ? addressService.convertAddressToAddressResponse(account.getAddress())
                                : null)
                .store(convertStoreTStoreResponse(store))
                .image(image)
                .build();
    }


    public StoreResponse convertStoreTStoreResponse(Store store) {
        if (store == null) {
            return null;
        }
        return StoreResponse.builder()
                .id(store.getId())
                .name(store.getName())
                .postalCode(store.getPostalCode())
                .phone(store.getPhone())
                .email(store.getEmail())
                .streetAddress(store.getStreetAddress())
                .description(store.getDescription())
                .urlImage(store.getImage() != null ? store.getImage().getFileDownloadUri() : null)
                .activeStatus(store.isActiveStatus())
                .build();
    }

    // Lấy địa chỉ
    public String getAddress(Address address) {
        if (address == null) {
            return "";
        }
        if (address.getWard() != null) {
            String addressWard = address.getWard().getName();
            String addressDistrict = address.getWard().getDistrict().getName();
            String addressProvince = address.getWard().getDistrict().getProvinceCity().getName();
            String addressAddress = address.getStreetAddress() == null ? "" : address.getStreetAddress() + ", ";
            return addressAddress + addressWard + ", " + addressDistrict + ", " +
                    addressProvince;
        }
        return "";

    }

    @SuppressWarnings("static-access")
    public ResponseDocumentDto changeAvatar(MultipartFile image) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Image avatar = account.getImage();
        if (avatar != null) {
            filesHelp.deleteFile(account.getId(), avatar.getFileId(), EntityFileType.USER_AVATAR);
        } else {
            avatar = new Image();
        }
        var fileSaved = filesHelp.saveFile(image, account.getId(), EntityFileType.USER_AVATAR);
        avatar.setFileId(fileSaved.getFileId());
        avatar.setFileName(fileSaved.getFileName());
        avatar.setFileType(fileSaved.getFileType());
        avatar.setSize(fileSaved.getSize());
        avatar.setFileDownloadUri(fileSaved.getFileDownloadUri());

        var avatarUpdate = imageReponsitory.save(avatar);
        System.out.println(avatarUpdate.getFileDownloadUri());
        account.setImage(avatar);
        accountRepository.save(account);

        return ImageDtoUtil.convertToImageResponse(avatar);
    }

}
