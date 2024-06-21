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
        Store save = storeRepository.save(store);

        if (file.getOriginalFilename() != null) {
            filesHelp.saveFile(file, save.getId(), EntityFileType.STORE_LOGO);
        }
        List<ResponseDocumentDto> urlImage = filesHelp.getDocuments(save.getId(), EntityFileType.STORE_LOGO);

        StoreResponse response = mapper.map(store, StoreResponse.class);
        response.setUrlImage(urlImage.get(0));
        return response;

    }

    @SuppressWarnings("static-access")
    public StoreResponse updateImage(Long id, MultipartFile file) {
        Store store = storeRepository.findById(id).orElseThrow(() -> {
            throw new AppException(ErrorCode.STORE_NOT_FOUND);
        });
        List<ResponseDocumentDto> image = filesHelp.getDocuments(id, EntityFileType.STORE_LOGO);
        if (image.size() > 0) {
            filesHelp.deleteFile(id, image.get(0).getFileId(), EntityFileType.STORE_LOGO);
            if (file.getOriginalFilename() != null) {
                filesHelp.saveFile(file, store.getId(), EntityFileType.STORE_LOGO);
            }
        }
        List<ResponseDocumentDto> urlImage = filesHelp.getDocuments(store.getId(), EntityFileType.STORE_LOGO);
        StoreResponse response = mapper.map(store, StoreResponse.class);
        response.setUrlImage(urlImage.get(0));
        return response;

    }
    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }
    
}
