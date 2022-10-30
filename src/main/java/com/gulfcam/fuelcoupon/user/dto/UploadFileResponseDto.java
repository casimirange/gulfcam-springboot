package com.gulfcam.fuelcoupon.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UploadFileResponseDto {
	
	public UploadFileResponseDto(String fileName, String fileType, long size,String fileDownloadUri) {
		super();
		this.fileName = fileName;
		this.fileType = fileType;
		this.size = size;
		this. fileDownloadUri = fileDownloadUri;
	}
	private String fileName;
	private String fileDownloadUri;
	private String fileType;
	private long size;

}
