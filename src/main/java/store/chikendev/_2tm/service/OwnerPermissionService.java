package store.chikendev._2tm.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import store.chikendev._2tm.dto.request.OwnerPermissionRequest;
import store.chikendev._2tm.dto.responce.AccountResponse;
import store.chikendev._2tm.dto.responce.OwnerPermissionResponse;
import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.entity.OwnerPermission;
import store.chikendev._2tm.entity.Role;
import store.chikendev._2tm.entity.RoleAccount;
import store.chikendev._2tm.entity.StateOwnerPermission;
import store.chikendev._2tm.exception.AppException;
import store.chikendev._2tm.exception.ErrorCode;
import store.chikendev._2tm.repository.AccountRepository;
import store.chikendev._2tm.repository.OwnerPermissionRepository;
import store.chikendev._2tm.repository.RoleAccountRepository;
import store.chikendev._2tm.repository.RoleRepository;
import store.chikendev._2tm.repository.StateOwnerPermissionRepository;
import store.chikendev._2tm.utils.service.AccountServiceUtill;

@Service
public class OwnerPermissionService {

    @Autowired
    private OwnerPermissionRepository ownerRep;

    @Autowired
    private StateOwnerPermissionRepository stateRep;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private StateOwnerPermissionRepository stateOwnerPermissionRepository;

    @Autowired
    private RoleAccountRepository roleAccountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AccountServiceUtill accountServiceUtill;

    public OwnerPermissionResponse addOwnerPermission(
        OwnerPermissionRequest request
    ) {
        String email = SecurityContextHolder.getContext()
            .getAuthentication()
            .getName();
        Account account = accountRepository
            .findByEmail(email)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        OwnerPermission found = ownerRep.findByAccountId(account.getId());
        if (found != null) {
            return OwnerPermissionResponse.builder()
                .id(found.getId())
                .bankName(found.getBankName())
                .BankAccountNumber(found.getBankAccountNumber())
                .accountHolderName(found.getAccountHolderName())
                .state(found.getState().getDescription())
                .createdAt(found.getCreatedAt())
                .build();
        }

        OwnerPermission ownerPermission = new OwnerPermission();
        ownerPermission.setBankName(request.getBankName());
        ownerPermission.setBankAccountNumber(request.getBankAccountNumber());
        ownerPermission.setAccountHolderName(request.getAccountHolderName());
        StateOwnerPermission state = stateRep
            .findById(StateOwnerPermission.IN_CONFIRM)
            .get();
        ownerPermission.setState(state);
        ownerPermission.setAccount(account);
        OwnerPermission save = ownerRep.save(ownerPermission);

        if (save == null) {
            throw new AppException(ErrorCode.OWNER_PERMISSION_NOT_FOUND);
        }

        return null;
    }

    public Page<OwnerPermissionResponse> getOwnerPer(
        int size,
        int page,
        Long stateId
    ) {
        Pageable pageable = PageRequest.of(page, size);
        if (stateId == null) {
            Page<OwnerPermission> responses = ownerRep.findAll(pageable);
            return convertToResponse(responses);
        } else {
            StateOwnerPermission state = stateRep
                .findById(stateId)
                .orElseThrow(() -> new AppException(ErrorCode.STATE_NOT_FOUND));
            Page<OwnerPermission> responses = ownerRep.findByStateId(
                state,
                pageable
            );
            return convertToResponse(responses);
        }
    }

    private Page<OwnerPermissionResponse> convertToResponse(
        Page<OwnerPermission> response
    ) {
        return response.map(ownerPermission -> {
            return getOwnerPermissionResponse(ownerPermission);
        });
    }

    public OwnerPermissionResponse getOwnerPermissionResponse(
        OwnerPermission ownerPermission
    ) {
        AccountResponse accountResponse = accountServiceUtill.convertToResponse(
            ownerPermission.getAccount()
        );

        return OwnerPermissionResponse.builder()
            .id(ownerPermission.getId())
            .bankName(ownerPermission.getBankName())
            .BankAccountNumber(ownerPermission.getBankAccountNumber())
            .accountHolderName(ownerPermission.getAccountHolderName())
            .state(ownerPermission.getState().getDescription())
            .createdAt(ownerPermission.getCreatedAt())
            .accountResponse(accountResponse)
            .build();
    }

    public void cancelOwnerPermission(Long id) {
        String email = SecurityContextHolder.getContext()
            .getAuthentication()
            .getName();
        Account account = accountRepository
            .findByEmail(email)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        List<RoleAccount> role = roleAccountRepository.findByAccount(account);
        boolean checkRole = false;
        for (RoleAccount roleAccount : role) {
            if (roleAccount.getRole().getId().equals(Role.ROLE_ADMIN)) {
                checkRole = true;
            }
        }
        if (!checkRole) {
            throw new AppException(ErrorCode.NO_MANAGEMENT_RIGHTS);
        }
        OwnerPermission ownerPermission = ownerRep
            .findById(id)
            .orElseThrow(() ->
                new AppException(ErrorCode.OWNER_PERMISSION_NOT_FOUND)
            );
        Long currentState = ownerPermission.getState().getId();

        if (!currentState.equals(StateOwnerPermission.IN_CONFIRM)) {
            throw new AppException(ErrorCode.STATE_ERROR);
        }
        StateOwnerPermission cancelState = stateOwnerPermissionRepository
            .findById(StateOwnerPermission.REFUSE)
            .orElseThrow(() -> new AppException(ErrorCode.STATE_NOT_FOUND));

        ownerPermission.setState(cancelState);
        ownerRep.save(ownerPermission);
    }

    public void confirmOwnerPermission(Long id) {
        OwnerPermission ownerPermission = ownerRep
            .findById(id)
            .orElseThrow(() ->
                new AppException(ErrorCode.OWNER_PERMISSION_NOT_FOUND)
            );
        String email = SecurityContextHolder.getContext()
            .getAuthentication()
            .getName();
        Account account = accountRepository
            .findByEmail(email)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        List<RoleAccount> role = roleAccountRepository.findByAccount(account);
        boolean checkRole = false;
        for (RoleAccount roleAccount : role) {
            if (roleAccount.getRole().getId().equals(Role.ROLE_ADMIN)) {
                checkRole = true;
            }
        }
        if (!checkRole) {
            throw new AppException(ErrorCode.NO_MANAGEMENT_RIGHTS);
        }
        Long currentState = ownerPermission.getState().getId();
        if (!currentState.equals(StateOwnerPermission.IN_CONFIRM)) {
            throw new AppException(ErrorCode.STATE_ERROR);
        }

        StateOwnerPermission confirmState = stateOwnerPermissionRepository
            .findById(StateOwnerPermission.CONFIRM)
            .orElseThrow(() -> new AppException(ErrorCode.STATE_NOT_FOUND));
        ownerPermission.setState(confirmState);

        Account requesterAccount = ownerPermission.getAccount();
        Role productOwnerRole = roleRepository
            .findById(Role.ROLE_PRODUCT_OWNER)
            .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        RoleAccount roleAccount = new RoleAccount();
        roleAccount.setAccount(requesterAccount);
        roleAccount.setRole(productOwnerRole);
        roleAccountRepository.save(roleAccount);
        ownerRep.save(ownerPermission);
    }
}
