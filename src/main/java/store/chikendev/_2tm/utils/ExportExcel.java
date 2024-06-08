package store.chikendev._2tm.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

import java.lang.reflect.Field;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;

@Component
public class ExportExcel {

    @SuppressWarnings("hiding")
    public <T> List<T> saveToExcel(MultipartFile file, Class<T> clazz)
            throws EncryptedDocumentException, IOException, ReflectiveOperationException {
        List<List<String>> rows = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        rows = StreamSupport.stream(sheet.spliterator(), false)
                .skip(1)
                .map(row -> StreamSupport.stream(row.spliterator(), false).map(cell -> cell.toString()).toList())
                .toList();

        List<T> list = new ArrayList<>();

        for (List<String> row : rows) {
            T t = mapRowToEntity(row, clazz);
            list.add(t);
        }
        return list;
    }

    @SuppressWarnings("hiding")
    public <T> void exportToExcel(List<T> dataList, Class<T> clazz, HttpServletResponse response)
            throws IOException, IllegalAccessException {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Data");

        // Định dạng font in đậm
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);

        // Định dạng căn giữa
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);
        headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
        headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // Create header row
        Row headerRow = sheet.createRow(0);
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(fields[i].getName());
            cell.setCellStyle(headerCellStyle);
        }
        // Định dạng căn giữa cho các dòng dữ liệu
        CellStyle dataCellStyle = workbook.createCellStyle();
        dataCellStyle.setAlignment(HorizontalAlignment.CENTER);
        dataCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // Populate data rows
        int rowNum = 1;
        for (T data : dataList) {
            Row row = sheet.createRow(rowNum++);
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                Cell cell = row.createCell(i);
                Object value = fields[i].get(data);
                if (value != null) {
                    cell.setCellValue(value.toString());
                    cell.setCellStyle(dataCellStyle);
                }
            }
        }
        // Tự động điều chỉnh kích thước cột
        for (int i = 0; i < fields.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Thiết lập thông tin phản hồi để tải file về
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=data.xlsx");

        // Ghi workbook ra phản hồi
        workbook.write(response.getOutputStream());
        workbook.close();

    }

    @SuppressWarnings("hiding")
    private <T> T mapRowToEntity(List<String> row, Class<T> clazz) throws ReflectiveOperationException {
        T entity = clazz.getDeclaredConstructor().newInstance();
        Field[] fields = clazz.getDeclaredFields();

        for (int i = 0; i < row.size() && i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            String value = row.get(i);

            if (field.getType() == int.class || field.getType() == Integer.class) {
                field.set(entity, Integer.parseInt(value));
            } else if (field.getType() == double.class || field.getType() == Double.class) {
                field.set(entity, Double.parseDouble(value));
            } else if (field.getType() == long.class || field.getType() == Long.class) {
                field.set(entity, Long.parseLong(value));
            } else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                field.set(entity, Boolean.parseBoolean(value));
            } else {
                field.set(entity, value);
            }
        }
        return entity;
    }

}
