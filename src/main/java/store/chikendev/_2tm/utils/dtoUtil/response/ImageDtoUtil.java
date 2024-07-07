package store.chikendev._2tm.utils.dtoUtil.response;

import store.chikendev._2tm.dto.responce.ResponseDocumentDto;
import store.chikendev._2tm.entity.Image;

public class ImageDtoUtil {
    public static ResponseDocumentDto convertToImageResponse(Image image) {
        return ResponseDocumentDto.builder()
                .fileId(image.getFileId())
                .fileName(image.getFileName())
                .fileType(image.getFileType())
                .size(image.getSize())
                .fileDownloadUri(image.getFileDownloadUri())
                .build();
    }
}
