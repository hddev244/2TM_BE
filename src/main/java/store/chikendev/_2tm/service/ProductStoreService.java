package store.chikendev._2tm.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import store.chikendev._2tm.dto.responce.AttributeProductResponse;
import store.chikendev._2tm.dto.responce.ProductResponse;
import store.chikendev._2tm.dto.responce.ProductStoreResponse;
import store.chikendev._2tm.dto.responce.ResponseDocumentDto;
import store.chikendev._2tm.entity.Product;
import store.chikendev._2tm.entity.Store;
import store.chikendev._2tm.exception.AppException;
import store.chikendev._2tm.exception.ErrorCode;
import store.chikendev._2tm.repository.ProductRepository;
import store.chikendev._2tm.repository.StoreRepository;
import store.chikendev._2tm.utils.EntityFileType;
import store.chikendev._2tm.utils.FilesHelp;

@Service
public class ProductStoreService {
    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ProductRepository productRepository;

    public ProductStoreResponse getById(Long id) {
        Store store = storeRepository.findById(id).orElseThrow(() -> {
            throw new AppException(ErrorCode.STORE_NOT_FOUND);
        });
        List<ProductResponse> products = new ArrayList<>();

        if (store.getProducts() != null) {
            store.getProducts().forEach(product -> {
                products.add(getByIdProduct(product.getId()));
            });
        }

        ProductStoreResponse response = new ProductStoreResponse();
        response.setId(store.getId());
        response.setName(store.getName());
        response.setPhone(store.getPhone());
        response.setEmail(store.getEmail());
        response.setDescription(store.getDescription());
        response.setStreetAddress(getStoreAddress(store));
        response.setProducts(products);
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
            return storeAddress + StoreWard + ", " + StoreDistrict + ", " +
                    StoreProvince;
        }
        return "";
    }

    private ProductResponse convertToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setPrice(product.getPrice());
        response.setQuantity(product.getQuantity());
        response.setDescription(product.getDescription());
        if (product.getType() != null) {
            response.setTypeProduct(product.getType() ? "Cửa hàng" : "Ký gửi");
        }

        List<ResponseDocumentDto> responseDocument = FilesHelp.getDocuments(response.getId(), EntityFileType.PRODUCT);
        response.setImages(responseDocument);
        return response;
    }

    public ProductResponse getByIdProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        });

        List<AttributeProductResponse> attrs = new ArrayList<>();

        if (product.getAttributes().size() > 0) {
            product.getAttributes().forEach(att -> {
                attrs.add(AttributeProductResponse.builder()
                        .id(att.getAttributeDetail().getAttribute().getId())
                        .name(att.getAttributeDetail().getAttribute().getName())
                        .value(att.getAttributeDetail().getDescription())
                        .build());
            });
        }

        ProductResponse response = convertToResponse(product);
        response.setAttributes(attrs);
        response.setIdCategory(product.getCategory().getId());

        return response;

    }

}
