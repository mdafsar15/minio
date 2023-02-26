package com.genx.service;

import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.io.*;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xmlpull.v1.XmlPullParserException;

import com.jlefebure.spring.boot.minio.MinioException;
import com.jlefebure.spring.boot.minio.MinioService;

import io.minio.MinioClient;
import io.minio.Result;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidArgumentException;
import io.minio.errors.InvalidBucketNameException;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.NoResponseException;
import io.minio.http.Method;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;

import com.genx.constant.Constant;
import com.genx.response.GeneratedUrls;
import com.genx.service.FileServiceImpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

	@Autowired
	MinioService minioService;

	@Autowired
	MinioClient minioClient;

	@Override
	public String addFile(MultipartFile file) throws InvalidKeyException, NoSuchAlgorithmException,
			XmlPullParserException, io.minio.errors.MinioException {
		Path path = Path.of(file.getOriginalFilename());
		Map<String, String> header = new HashMap<>();
		header.put("X-Incident-Id", "C918371984");

		String geturl = null;
		try {
			minioService.upload(path, file.getInputStream(), file.getContentType(), header);
			geturl = getSingleUrl(file.getOriginalFilename());
			log.info("geturl " + geturl);
			
		} catch (MinioException e) {
			throw new IllegalStateException("The file cannot be upload on the internal storage. Please retry later", e);
		} catch (IOException e) {
			throw new IllegalStateException("The file cannot be read", e);
		}
		return geturl;
	}

	@Override
	public List<GeneratedUrls> getListUrls() throws NoSuchAlgorithmException, IOException, InvalidKeyException,
			XmlPullParserException, io.minio.errors.MinioException {

		String url = null;
		List<GeneratedUrls> list = new ArrayList<GeneratedUrls>();
		final String minioBucket = "lcb";

		minioClient = new MinioClient(Constant.HOST, Constant.IP, Constant.ACCESS_KEY, Constant.TOKEN);

		Iterable<Result<Item>> myObjects = minioClient.listObjects(minioBucket);

		for (Result<Item> result : myObjects) {
			Item item = result.get();
			System.out.println(item.lastModified() + ", " + item.size() + ", " + item.objectName());

			// Generate a presigned URL which expires in a day
			url = minioClient.presignedGetObject(minioBucket, item.objectName(), 60 * 60 * 24);

			GeneratedUrls generated = new GeneratedUrls();

			generated.setUrl(url);
			generated.setDescription(item.lastModified() + ", " + item.size() + ", " + item.objectName());

			// Add the album object to the list holding Album objects
			list.add(generated);

		}

		return list;
	}

	@Override
	public String getSingleUrl(String objectName) throws NoSuchAlgorithmException, IOException, InvalidKeyException,
			XmlPullParserException, io.minio.errors.MinioException {
		String url = null;
		final String minioBucket = "lcb";

		minioClient = new MinioClient(Constant.HOST, Constant.IP, Constant.ACCESS_KEY, Constant.TOKEN);

		// Generate a presigned URL which expires in a day
		url = minioClient.presignedGetObject(minioBucket, objectName, 60 * 60 * 24);

//		GeneratedUrls generated = new GeneratedUrls();

//		generated.setUrl(url);

		return url;
	}

	@Override
	public void removeSingleFile(String objectName) throws NoSuchAlgorithmException, IOException, InvalidKeyException,
			XmlPullParserException, InvalidEndpointException, InvalidPortException, InvalidBucketNameException,
			InsufficientDataException, NoResponseException, ErrorResponseException, InternalException,
			InvalidArgumentException, InvalidResponseException {

		final String minioBucket = "lcb";

		minioClient = new MinioClient(Constant.HOST, Constant.IP, Constant.ACCESS_KEY, Constant.TOKEN);

		minioClient.removeObject(minioBucket, objectName);

		System.out.println("successfully removed object");

	}

	@Override
	public GeneratedUrls getDownloadUrl(String objectName) throws NoSuchAlgorithmException, IOException,
			InvalidKeyException, XmlPullParserException, io.minio.errors.MinioException {

		String url = null;

		final String minioBucket = "lcb";

		minioClient = new MinioClient(Constant.HOST, Constant.IP, Constant.ACCESS_KEY, Constant.TOKEN);

		url = minioClient.getObjectUrl(minioBucket, objectName);
		System.out.println("Download URL :" + url);

		GeneratedUrls generated = new GeneratedUrls();

		generated.setUrl(url);

		return generated;

	}

}
