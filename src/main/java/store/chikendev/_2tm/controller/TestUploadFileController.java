package store.chikendev._2tm.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.dto.responce.ResponseDocumentDto;
import store.chikendev._2tm.utils.EntityFileType;
import store.chikendev._2tm.utils.FilesHelp;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/uploadfile")
public class TestUploadFileController {
    class FileUpload {
        private String fileName;
        private String fileDownloadUri;
        private String fileType;
        private long size;
    }

    private FileUpload getFileUpload() {
        return new FileUpload();
    }

    @GetMapping
    public ApiResponse<List<ResponseDocumentDto>> getDocuments(
            @RequestParam("entityId") String entityId
            ) {
        return new ApiResponse<>(200, null, FilesHelp.getDocuments(entityId, EntityFileType.PRODUCT));
    }
    

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public String uploadFile(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("entityId") String entityId
            ) {
        for (MultipartFile file : files) {
            FilesHelp.saveFile(file, entityId, EntityFileType.PRODUCT);
        }
        return "Upload file successfully";
    }

    @DeleteMapping(value = "/delete/{entityId}/{fileId}")
    public String deleteFile(
           @PathVariable("entityId") String entityId,
            @PathVariable("fileId") String fileId
            ) {
        FilesHelp.deleteFile(entityId, fileId, EntityFileType.PRODUCT);
        return "Delete file successfully";
    }
}
