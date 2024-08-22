package store.chikendev._2tm.utils.dtoUtil.response;

import org.springframework.stereotype.Service;

import store.chikendev._2tm.dto.responce.ResponseDocumentDto;
import store.chikendev._2tm.entity.Image;

@Service
public class ImageDtoUtil {
    public static ResponseDocumentDto convertToImageResponse(Image image) {
        if (image == null) {
            return null;
        }
        return ResponseDocumentDto.builder()
                .fileId(image.getFileId())
                .fileName(image.getFileName())
                .fileType(image.getFileType())
                .size(image.getSize())
                .fileDownloadUri(image.getFileDownloadUri())
                .build();
    }
}
