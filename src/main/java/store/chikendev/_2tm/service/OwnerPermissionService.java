package store.chikendev._2tm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import store.chikendev._2tm.dto.request.OwnerPermissionRequest;
import store.chikendev._2tm.dto.responce.OwnerPermissionResponse;
import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.entity.OwnerPermission;
import store.chikendev._2tm.entity.StateOwnerPermission;
import store.chikendev._2tm.exception.AppException;
import store.chikendev._2tm.exception.ErrorCode;
import store.chikendev._2tm.repository.AccountRepository;
import store.chikendev._2tm.repository.OwnerPermissionRepository;
import store.chikendev._2tm.repository.StateOwnerPermissionRepository;

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

    // @Autowired
    // private RoleAccountRepository roleAccountRepository;

    // @Autowired
    // private RoleRepository roleRepository;

    public OwnerPermissionResponse addOwnerPermission(OwnerPermissionRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        System.out.println(request.getBankAccountNumber());
        OwnerPermission ownerPermission = new OwnerPermission();
        ownerPermission.setBankName(request.getBankName());
        ownerPermission.setBankAccountNumber(request.getBankAccountNumber());
        ownerPermission.setAccountHolderName(request.getAccountHolderName());
        StateOwnerPermission state = stateRep.findById(StateOwnerPermission.IN_CONFIRM).get();
        ownerPermission.setState(state);
        ownerPermission.setAccount(account);
        OwnerPermission save = ownerRep.save(ownerPermission);

        return OwnerPermissionResponse.builder()
                .id(save.getId())
                .bankName(save.getBankName())
                .BankAccountNumber(save.getBankAccountNumber())
                .accountHolderName(save.getAccountHolderName())
                .state(save.getState().getDescription())
                .createdAt(save.getCreatedAt())
                .accountResponse(accountService.getAccountByToken())
                .build();
    }

    public Page<OwnerPermissionResponse> getOwnerPer(int size, int page, Long stateId) {
        Pageable pageable = PageRequest.of(page, size);
        if (stateId == null) {
            Page<OwnerPermission> responses = ownerRep.findAll(pageable);
            return convertToResponse(responses);
        }

        else {
            StateOwnerPermission state = stateRep.findById(stateId)
                    .orElseThrow(() -> new AppException(ErrorCode.STATE_NOT_FOUND));
            Page<OwnerPermission> responses = ownerRep.findByStateId(state, pageable);
            return convertToResponse(responses);
        }
    }

    private Page<OwnerPermissionResponse> convertToResponse(Page<OwnerPermission> response) {
        return response.map(ownerPermission -> {
            return getOwnerPermissionResponse(ownerPermission);
        });
    }

    public OwnerPermissionResponse getOwnerPermissionResponse(OwnerPermission ownerPermission) {
        return OwnerPermissionResponse.builder()
                .id(ownerPermission.getId())
                .bankName(ownerPermission.getBankName())
                .BankAccountNumber(ownerPermission.getBankAccountNumber())
                .accountHolderName(ownerPermission.getAccountHolderName())
                .state(ownerPermission.getState().getDescription())
                .createdAt(ownerPermission.getCreatedAt())
                .accountResponse(accountService.getAccountByToken())
                .build();
    }

    // public String updateState(Long id, UpdateStateOwwnerPermissionRequest
    // request) {
    // OwnerPermission ownerPermission = ownerRep.findById(id)
    // .orElseThrow(() -> new AppException(ErrorCode.OWNER_PERMISSION_NOT_FOUND));
    // if (ownerPermission != null) {
    // StateOwnerPermission state = stateRep.findById(request.getIdState())
    // .orElseThrow(() -> new AppException(ErrorCode.STATE_NOT_FOUND));
    // ownerPermission.setState(state);
    // OwnerPermission saveState = ownerRep.save(ownerPermission);
    // if (saveState.getState().getId() == 2) {
    // RoleAccount roleAccount = new RoleAccount();
    // Account account = accountRepository.findById(saveState.getAccount().getId())
    // .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    // Role role = roleRepository.findById("CH").orElseThrow(() -> new
    // AppException(ErrorCode.ROLE_NOT_FOUND));
    // roleAccount.setAccount(account);
    // roleAccount.setRole(role);
    // roleAccountRepository.save(roleAccount);
    // return "Thêm vài trò thành công";
    // }
    // return "Thay đỗi trạng thái yêu cầu thành công";
    // }
    // return "Thay đổi trạng thái yêu cầu thất bại";
    // }
}
