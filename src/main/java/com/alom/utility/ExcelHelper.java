package com.alom.utility;

import java.io.FileInputStream;
import java.io.FileOutputStream;
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

import com.alom.dto.AttendeeDto;
import com.alom.exception.ExcelFileReadWriteException;
import com.alom.exception.FileNotFoundException;
import com.alom.model.AttendeeModel;


public class ExcelHelper {
	
	
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
			throw new ExcelFileReadWriteException("unsupported file format - size mismatch.");
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
			throw new ExcelFileReadWriteException("unsupported file format - header fields mismatch.");
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
	 
	 /**
	  * <p> write a excel file using attendees data that are added while creating events</p>
	  * @param attendees
	  * @param filePath
	  * @param headerList
	  * @throws IOException
	  */
	 public static void writeAttendeesToExcel(List<AttendeeDto> attendees, String filePath, List<String> headerList) throws IOException {
		 
	        Workbook workbook = new XSSFWorkbook(); // Create a new workbook
	        Sheet sheet = workbook.createSheet("Attendees"); // Create a sheet

	        // Create header row
	        Row headerRow = sheet.createRow(0);
	        
	        for (int i = 0; i < headerList.size(); i++) {
	            Cell cell = headerRow.createCell(i);
	            cell.setCellValue(headerList.get(i));
	        }

	        // Fill in attendee data
	        for (int i = 0; i < attendees.size(); i++) {
	            AttendeeDto attendee = attendees.get(i);
	            Row row = sheet.createRow(i + 1); // Start from the second row

	            row.createCell(0).setCellValue(attendee.getName());
	            row.createCell(1).setCellValue(attendee.getContactNumber());
	            row.createCell(2).setCellValue(attendee.getBusinessTitle());
	            row.createCell(3).setCellValue(attendee.getCity());
	        }

	        // Write the output to a file
	        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
	            workbook.write(fileOut);
	        } finally {
	            workbook.close(); // Close the workbook
	        }
	    } 
}
