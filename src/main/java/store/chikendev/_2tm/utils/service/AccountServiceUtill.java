package store.chikendev._2tm.utils.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import store.chikendev._2tm.dto.responce.AccountResponse;
import store.chikendev._2tm.dto.responce.RoleResponse;
import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.entity.Address;
import store.chikendev._2tm.exception.AppException;
import store.chikendev._2tm.exception.ErrorCode;
import store.chikendev._2tm.repository.AccountRepository;
import store.chikendev._2tm.repository.RoleAccountRepository;
import store.chikendev._2tm.utils.dtoUtil.response.ImageDtoUtil;

@Service
public class AccountServiceUtill {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ImageDtoUtil imageDtoUtil;

    public Account getAccount() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        Account account = accountRepository
                .findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return account;
    }

    public AccountResponse convertToResponse(Account account) {
        if (account == null) {
            return null;
        }
        return AccountResponse.builder()
                .id(account.getId())
                .username(account.getUsername())
                .fullName(account.getFullName())
                .phoneNumber(account.getPhoneNumber())
                .email(account.getEmail())
                .roles(account.getRoles().stream().map(roleAccount -> {
                    return RoleResponse.builder()
                            .id(roleAccount.getRole().getId())
                            .name(roleAccount.getRole().getName())
                            .build();
                })
                        .toList())
                .address(getAddress(account.getAddress()))
                .violationPoints(account.getViolationPoints())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .stateName(account.getState().getName())
                .image(imageDtoUtil.convertToImageResponse(account.getImage()))
                .build();
    }

    public String getAddress(Address address) {
        if (address == null) {
            return "";
        }
        if (address.getWard() != null) {
            String addressWard = address.getWard().getName();
            String addressDistrict = address.getWard().getDistrict().getName();
            String addressProvince = address
                    .getWard()
                    .getDistrict()
                    .getProvinceCity()
                    .getName();
            String addressAddress = address.getStreetAddress() == null
                    ? ""
                    : address.getStreetAddress() + ", ";
            return (addressAddress +
                    addressWard +
                    ", " +
                    addressDistrict +
                    ", " +
                    addressProvince);
        }
        return "";
    }
}
