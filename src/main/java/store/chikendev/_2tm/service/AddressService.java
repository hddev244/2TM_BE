package store.chikendev._2tm.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import store.chikendev._2tm.dto.request.AddressRequest;
import store.chikendev._2tm.dto.responce.AddressResponse;
import store.chikendev._2tm.dto.responce.CartResponse;
import store.chikendev._2tm.dto.responce.DistrictResponse;
import store.chikendev._2tm.dto.responce.ProvinceCityResponse;
import store.chikendev._2tm.dto.responce.WardResponse;
import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.entity.Address;
import store.chikendev._2tm.entity.CartItems;
import store.chikendev._2tm.entity.District;
import store.chikendev._2tm.entity.ProvinceCity;
import store.chikendev._2tm.entity.Ward;
import store.chikendev._2tm.exception.AppException;
import store.chikendev._2tm.exception.ErrorCode;
import store.chikendev._2tm.repository.AccountRepository;
import store.chikendev._2tm.repository.AddressRepository;
import store.chikendev._2tm.repository.DistrictRepository;
import store.chikendev._2tm.repository.ProvinceCityRepository;
import store.chikendev._2tm.repository.WardRepository;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private ProvinceCityRepository provinceCity;

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private WardRepository wardRepository;

    @Autowired
    private AccountRepository accountRepository;

    public List<DistrictResponse> getAll() {
        List<District> districts = districtRepository.findAll();
        return districts.stream().map(district -> mapper.map(district, DistrictResponse.class)).toList();
    }

    public ProvinceCityResponse getByIdProvince(Long id) {
        ProvinceCity province = provinceCity.findById(id)
                .orElseThrow(() -> new RuntimeException("Could not find ProvinceCity"));
        return mapper.map(province, ProvinceCityResponse.class);
    }

    public List<DistrictResponse> findDistrictByProvinceId(Long id) {
        System.out.println(id);
        List<District> districts = districtRepository.getDistrictByProvinceId(id);
        return districts.stream().map(district -> mapper.map(district, DistrictResponse.class)).toList();
    }

    public List<WardResponse> findByDistrictId(Long id) {
        System.out.println(id);
        List<Ward> wards = wardRepository.getByDistrictId(id);
        return wards.stream().map(ward -> mapper.map(ward, WardResponse.class)).toList();
    }

    public AddressResponse addAddress(AddressRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Ward ward = wardRepository.findById(request.getWardId())
                .orElseThrow(() -> new AppException(ErrorCode.WARD_NOT_FOUND));
        Address address = new Address();
        address.setStreetAddress(request.getStreetAddress());
        address.setWard(ward);
        address.setAccount(account);

        Address save = addressRepository.save(address);

        AddressResponse response = new AddressResponse();
        response.setId(save.getId());
        response.setName(getAddress(save));

        return response;
    }

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

}
