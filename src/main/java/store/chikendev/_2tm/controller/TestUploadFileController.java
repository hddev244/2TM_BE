package store.chikendev._2tm.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.dto.responce.ResponseDocumentDto;
import store.chikendev._2tm.entity.Image;
import store.chikendev._2tm.entity.ProductImages;
import store.chikendev._2tm.repository.ImageRepository;
import store.chikendev._2tm.repository.ProductImagesRepository;
import store.chikendev._2tm.repository.ProductRepository;
import store.chikendev._2tm.utils.EntityFileType;
import store.chikendev._2tm.utils.FilesHelp;
import store.chikendev._2tm.utils.Payment;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/test")
public class TestUploadFileController {
    @Autowired
    private Payment payment;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ProductImagesRepository productImagesRepository;

    @GetMapping("vnpay-create-payment")
    public String getMethodName()
            throws Exception {
        return new String(payment.createVNPT(12200000L,
                "11111"));
    }

    @GetMapping
    public ApiResponse<List<ResponseDocumentDto>> getDocuments(
            @RequestParam("entityId") String entityId) {
        List<ResponseDocumentDto> responseDocumentDtos = FilesHelp.getDocuments(entityId, EntityFileType.PRODUCT);
        // Long id = Long.parseLong(entityId);
        // var product = productRepository.findById(id).get();

        // List<ProductImages> productImages = new ArrayList<>();

        // for (ResponseDocumentDto fileSaved : responseDocumentDtos) {
        // Image image = Image.builder()
        // .fileId(fileSaved.getFileId())
        // .fileName(fileSaved.getFileName())
        // .fileDownloadUri(fileSaved.getFileDownloadUri())
        // .fileType(fileSaved.getFileType())
        // .size(fileSaved.getSize())
        // .build();
        // Image imageSaved = imageRepository.save(image);

        // ProductImages productImage = ProductImages.builder()
        // .product(product)
        // .image(imageSaved)
        // .build();
        // productImages.add(productImage);
        // }

        // productImagesRepository.saveAll(productImages);

        return new ApiResponse<>(200, null, responseDocumentDtos);
    }

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public String uploadFile(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("entityId") String entityId) {
        for (MultipartFile file : files) {
            FilesHelp.saveFile(file, entityId, EntityFileType.PRODUCT);
        }
        return "Upload file successfully";
    }

    @DeleteMapping(value = "/delete/{entityId}/{fileId}")
    public String deleteFile(
            @PathVariable("entityId") String entityId,
            @PathVariable("fileId") String fileId) {
        FilesHelp.deleteFile(entityId, fileId, EntityFileType.PRODUCT);
        return "Delete file successfully";
    }
}
