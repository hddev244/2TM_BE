package store.chikendev._2tm.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import store.chikendev._2tm.dto.responce.ResponseDocumentDto;
import store.chikendev._2tm.exception.AppException;
import store.chikendev._2tm.exception.ErrorCode;

@Component
public class FilesHelp {
    public static String getUniqueFileName(String originalFileName) {
        return UUID.randomUUID().toString() + "-" + originalFileName.replaceAll("\\s+", "_");
    }

    public static void saveFile(MultipartFile file, Object entityId, EntityFileType type) {
        if (entityId == null || entityId.toString().trim().isEmpty()) {
            throw new AppException(ErrorCode.FILE_UPLOAD_ERROR);
        }
        String dir = type.getDir() + entityId;
        try {
            File directoryFile = new File(dir);
            if (!directoryFile.exists()) {
                directoryFile.mkdirs();
            } else if (!type.isMultiple()) {
                for (Path item : Files.list(Paths.get(dir)).collect(Collectors.toList())) {
                    item.toFile().delete();
                }
            }
            Files.copy(file.getInputStream(),
                    Paths.get(dir + "/" + getUniqueFileName(file.getOriginalFilename())),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new AppException(ErrorCode.FILE_UPLOAD_ERROR);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static List<ResponseDocumentDto> getDocuments(Object entityId, EntityFileType type) {
        if (entityId == null || type == null || entityId.toString().trim().isEmpty()) {
           return new ArrayList<ResponseDocumentDto>();
        }
        Path path = Paths.get(type.getDir() + entityId);
        List<ResponseDocumentDto> responseDocumentDtos = new ArrayList();
        try {
            for (Path item : Files.list(path).collect(Collectors.toList())) {
                String fileNameReal = item.getFileName().toString().substring(37);
                String fileIdReal = item.getFileName().toString().substring(0, 36);
                ResponseDocumentDto responseDocumentDto = ResponseDocumentDto.builder()
                        .fileName(fileNameReal)
                        .fileDownloadUri(type.getDir() + entityId + "/" + item.getFileName().toString())
                        .fileType(Files.probeContentType(item))
                        .fileId(fileIdReal)
                        .size(Files.size(item))
                        .build();
                responseDocumentDtos.add(responseDocumentDto);
            }
            return responseDocumentDtos;
        } catch (Exception e) {
            return new ArrayList<ResponseDocumentDto>();
            // throw new AppException(ErrorCode.FILE_UPLOAD_ERROR);
        }
    }

    public static ResponseDocumentDto getOneDocument(Object entityId, EntityFileType type){
        List<ResponseDocumentDto> responseDocumentDtos = getDocuments(entityId, type);
        if (responseDocumentDtos.size() != 0) {
            return responseDocumentDtos.get(0);
        }
        return new ResponseDocumentDto();
    }

    @SuppressWarnings("unused")
    public static void deleteFile(Object entityId, Object fileId, EntityFileType type) {
        if (entityId == null || fileId == null || type == null || entityId.toString().trim().isEmpty()
                || fileId.toString().trim().isEmpty()) {
            throw new AppException(ErrorCode.FILE_UPLOAD_ERROR);
        }
        Path path = Paths.get(type.getDir() + entityId.toString().trim());
        try {
            for (Path item : Files.list(path).collect(Collectors.toList())) {
                String fileNameReal = item.getFileName().toString().substring(37);
                String fileIdReal = item.getFileName().toString().substring(0, 36);
                if (fileIdReal.equals(fileId)) {
                    item.toFile().delete();
                }
            }
        } catch (IOException ioException) {
        }
    }
}
