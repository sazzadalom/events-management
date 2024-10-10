package com.alom.utility;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.alom.exception.ExcelFileReadingException;
import com.alom.exception.FileNotFoundException;
import com.alom.model.AttendeeModel;


public class ExcelUtility {
	
	
	public static void validateFileExtention(String originalFilename) {
		String extension = FilenameUtils.getExtension(originalFilename);
		if(!"xlsx".equalsIgnoreCase(extension)) {
			throw new FileNotFoundException("unsupported file format.");
		}	
	}
	
	public static void validateHeaderContents(Workbook workbook, List<String> headerList) {

		// WORKBOOK HEADER LIST THAT IS COMING FROM BULK UPLOAD.
		List<String> uploadHeaderList = getUploadFileHeader(workbook);
//		log.info("excel header size: {} | upload header size: {}", headerList.size(), uploadHeaderList.size());

		// IF VALIDATION OF SIZE OF EXCEL HEADER FAILS, WE ARE RETURNING THE RESPONSE AS FALSE.
		if (uploadHeaderList.size() != headerList.size()) {
			throw new ExcelFileReadingException("unsupported file format - size mismatch.");
		}
		
		// CHECKING THE WORKBOOK CONTENTS IF THEY ARE EQUAL OR NOT.
		boolean flag = false;

		for (int i = 0; i < uploadHeaderList.size(); ++i) {
			if (!uploadHeaderList.get(i).trim().equals(headerList.get(i).trim())) {
//				log.error("header content not equal at index: {} | incomming header content: {} with size: {} | exit excel header content: {} with size: {}", i, uploadHeaderList.get(i).trim(), uploadHeaderList.get(i).trim().length(), headerList.get(i).trim(), headerList.get(i).trim().length());
				flag = true;
				break;
			}
		}

		if(flag) {
			throw new ExcelFileReadingException("unsupported file format - header fields mismatch.");
		}
	}

	
	private static List<String> getUploadFileHeader(Workbook workbook){
		List<String> uploadHeaderList = new ArrayList<>();

		Iterator<Row> rowIterator = workbook.getSheetAt(0).iterator();
		DataFormatter dataFormatter = new DataFormatter();
		
		if (rowIterator.hasNext()) {
			Iterator<Cell> cellIterator = rowIterator.next().iterator();

			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				uploadHeaderList.add(dataFormatter.formatCellValue(cell));
			}
		}
		
		return uploadHeaderList;
	}
	
	 public static List<AttendeeModel> readExcelFile(String filePath) throws IOException {
	        List<AttendeeModel> attendees = new ArrayList<>();
	        FileInputStream fis = new FileInputStream(filePath);
	        Workbook workbook = new XSSFWorkbook(fis);
	        Sheet sheet = workbook.getSheetAt(0);

	        for (Row row : sheet) {
	            if (row.getRowNum() == 0) continue; // Skip header row
	            AttendeeModel attendee = new AttendeeModel();
	            attendee.setName(row.getCell(0).getStringCellValue());
	            attendee.setContactNumber(row.getCell(1).getStringCellValue());
	            attendee.setBusinessTitle(row.getCell(2).getStringCellValue());
	            attendee.setCity(row.getCell(3).getStringCellValue());
	            attendees.add(attendee);
	        }

	        workbook.close();
	        return attendees;
	    }
	 
	 
}
