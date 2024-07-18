package store.chikendev._2tm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import store.chikendev._2tm.dto.request.NotificationPayload;
import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.dto.responce.ResponseDocumentDto;
import store.chikendev._2tm.repository.AccountRepository;
import store.chikendev._2tm.service.AuthenticationService;
import store.chikendev._2tm.service.NotificationService;
import store.chikendev._2tm.utils.EntityFileType;
import store.chikendev._2tm.utils.FilesHelp;
import store.chikendev._2tm.utils.Payment;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/test")
public class TestUploadFileController {
    @Autowired
    NotificationService notificationService;

    @Autowired
    private Payment payment;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    AccountRepository accountRepository;
    // thoi gian hieu luc cua token
    @Value("${jwt.valid-duration}")
    private Long VALID_DURATION;

    // thoi gian con hieu luc de lam moi token
    @Value("${jwt.refreshable-duration}")
    private Long REFRESHABLE_DURATION;

    @GetMapping("vnpay-create-payment")
    public String getMethodName()
            throws Exception {
        return new String(payment.createVNPT(12200000L,
                "11111"));
    }

    @PostMapping("refreshtoken")
    public ApiResponse<String> refreshToken() {
            authenticationService.refreshToKenFromHttpServletRequest();

        return new ApiResponse<>(200, null, "Refresh token successfully");
    }

    @GetMapping("notification")
    public String callCreateNotification() {
        NotificationPayload payload = NotificationPayload.builder()
                .objectId("111111111") // là id của order, thanh toán, ...
                .accountId("c6cd2076-4c0b-4b9b-b345-374640888ba0")
                .message("Test thông báo . lỗi cors origin") // nội dung thông báo
                .type(NotificationPayload.TYPE_ORDER)  // loại thông báo theo objectId (order, payment, ...)
                .build();

        return notificationService.callCreateNotification(payload);
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
