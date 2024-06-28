package store.chikendev._2tm.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import store.chikendev._2tm.dto.request.StoreRequest;
import store.chikendev._2tm.dto.responce.ResponseDocumentDto;
import store.chikendev._2tm.dto.responce.StoreResponse;
import store.chikendev._2tm.entity.Store;
import store.chikendev._2tm.entity.Ward;
import store.chikendev._2tm.exception.AppException;
import store.chikendev._2tm.exception.ErrorCode;
import store.chikendev._2tm.repository.StoreRepository;
import store.chikendev._2tm.repository.WardRepository;
import store.chikendev._2tm.utils.EntityFileType;
import store.chikendev._2tm.utils.FilesHelp;

@Service
public class StoreService {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private FilesHelp filesHelp;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private WardRepository wardRepository;

    @SuppressWarnings("static-access")
    public StoreResponse createStore(StoreRequest request, MultipartFile file) {
        Store store = mapper.map(request, Store.class);
        if (request.getIdWard() != null) {
            Ward ward = wardRepository.findById(request.getIdWard())
                    .orElseThrow(() -> new AppException(ErrorCode.WARD_NOT_FOUND));
            store.setWard(ward);
        }
        store.setActiveStatus(true);

        Store save = storeRepository.save(store);

        if (file.getOriginalFilename() != null) {
            filesHelp.saveFile(file, save.getId(), EntityFileType.STORE_LOGO);
        }
        ResponseDocumentDto urlImage = filesHelp.getOneDocument(save.getId(), EntityFileType.STORE_LOGO);

        StoreResponse response = mapper.map(store, StoreResponse.class);
        response.setUrlImage(urlImage.getFileDownloadUri());
        return response;

    }

    @SuppressWarnings("static-access")
    public StoreResponse updateImage(Long id, MultipartFile file) {
        Store store = storeRepository.findById(id).orElseThrow(() -> {
            throw new AppException(ErrorCode.STORE_NOT_FOUND);
        });
        if (file.getOriginalFilename() != null) {
            filesHelp.saveFile(file, store.getId(), EntityFileType.STORE_LOGO);
        }
        ResponseDocumentDto urlImage = filesHelp.getOneDocument(store.getId(), EntityFileType.STORE_LOGO);
        StoreResponse response = mapper.map(store, StoreResponse.class);
        response.setUrlImage(urlImage.getFileDownloadUri());
        return response;

    }

    @SuppressWarnings("static-access")
    public List<StoreResponse> getAllStores() {
        List<Store> stores = storeRepository.findAll();
        List<StoreResponse> response = stores.stream().map(store -> {
            StoreResponse storeResponse = mapper.map(store, StoreResponse.class);
            storeResponse.setStreetAddress(getStoreAddress(store));
            ResponseDocumentDto urlImage = filesHelp.getOneDocument(store.getId(), EntityFileType.STORE_LOGO);
            storeResponse.setUrlImage(urlImage.getFileDownloadUri());
            return storeResponse;
        }).toList();
        return response;

    }

    private String getStoreAddress(Store store) {
        if (store == null) {
            return "";
        }
        if (store.getWard() != null) {
            String StoreWard = store.getWard().getName();
            String StoreDistrict = store.getWard().getDistrict().getName();
            String StoreProvince = store.getWard().getDistrict().getProvinceCity().getName();
            String storeAddress = store.getStreetAddress() == null ? "" : store.getStreetAddress() + ", ";
            return storeAddress + StoreWard + ", " + StoreDistrict + ", " + StoreProvince;
        }
        return "";
    }

    public List<StoreResponse> getStoreByDistrictId(Long dictrictId) {
        List<Store> stores = storeRepository.findByDictrictId(dictrictId);

        if (stores.isEmpty()) {
            throw new AppException(ErrorCode.STORE_NOT_FOUND);
        }

        @SuppressWarnings("static-access")
        List<StoreResponse> response = stores.stream().map(store -> {
            StoreResponse storeResponse = mapper.map(store, StoreResponse.class);
            storeResponse.setStreetAddress(getStoreAddress(store));
            ResponseDocumentDto urlImage = filesHelp.getOneDocument(store.getId(), EntityFileType.STORE_LOGO);
            storeResponse.setUrlImage(urlImage.getFileDownloadUri());
            return storeResponse;
        }).toList();
        return response;
    }

    // private StoreResponse convertToResponse(Store store) {
    // StoreResponse storeResponse = new StoreResponse();
    // storeResponse.setId(store.getId());
    // storeResponse.setName(store.getName());
    // storeResponse.setPostalCode(store.getPostalCode());
    // storeResponse.setPhone(store.getPhone());
    // storeResponse.setEmail(store.getEmail());
    // storeResponse.setStreetAddress(store.getStreetAddress());
    // storeResponse.setDescription(store.getDescription());
    // return storeResponse;
    // }

}
