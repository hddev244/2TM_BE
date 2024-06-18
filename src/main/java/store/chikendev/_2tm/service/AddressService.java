package store.chikendev._2tm.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import store.chikendev._2tm.dto.responce.DistrictResponse;
import store.chikendev._2tm.dto.responce.ProvinceCityResponse;
import store.chikendev._2tm.dto.responce.WardResponse;
import store.chikendev._2tm.entity.District;
import store.chikendev._2tm.entity.ProvinceCity;
import store.chikendev._2tm.entity.Ward;
import store.chikendev._2tm.repository.DistrictRepository;
import store.chikendev._2tm.repository.ProvinceCityRepository;
import store.chikendev._2tm.repository.WardRepository;

@Service
public class AddressService {

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private ProvinceCityRepository provinceCity;

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private WardRepository wardRepository;

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

}
