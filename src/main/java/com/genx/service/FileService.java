package com.genx.service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import org.xmlpull.v1.XmlPullParserException;

import com.genx.response.GeneratedUrls;

import io.minio.errors.MinioException;

public interface FileService {

	public String addFile(MultipartFile file) throws InvalidKeyException, NoSuchAlgorithmException, XmlPullParserException, io.minio.errors.MinioException;

	public List<GeneratedUrls> getListUrls()
			throws NoSuchAlgorithmException, IOException, InvalidKeyException, XmlPullParserException, MinioException;

	public String getSingleUrl(String objectName)
			throws NoSuchAlgorithmException, IOException, InvalidKeyException, XmlPullParserException, MinioException;

	public void removeSingleFile(String objectName)
			throws NoSuchAlgorithmException, IOException, InvalidKeyException, XmlPullParserException, MinioException;

	public GeneratedUrls getDownloadUrl(String objectName)
			throws NoSuchAlgorithmException, IOException, InvalidKeyException, XmlPullParserException, MinioException;

}
