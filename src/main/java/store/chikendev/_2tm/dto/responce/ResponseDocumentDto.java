package store.chikendev._2tm.dto.responce;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDocumentDto {
    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private String fileId;
    private long size;
}
