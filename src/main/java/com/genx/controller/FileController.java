package com.genx.controller;

import com.genx.response.GeneratedUrls;
import com.genx.service.FileServiceImpl;
import com.google.api.client.util.IOUtils;
import com.jlefebure.spring.boot.minio.MinioException;
import com.jlefebure.spring.boot.minio.MinioService;

import io.minio.messages.Item;
import io.swagger.v3.oas.annotations.Operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/v1/api")
public class FileController {

	@Autowired
	MinioService minioService;

	@Autowired
	FileServiceImpl fileServiceImpl;

	@GetMapping("/all")
	public List<Item> testMinio() {
		return minioService.list();
	}

	@PostMapping(value = "/UploadFile", consumes = { "multipart/form-data" })
	@Operation(summary = "Upload a single File")
	public String addAttachement(@RequestParam(value = "file", required = false) MultipartFile file)
			throws InvalidKeyException, NoSuchAlgorithmException, XmlPullParserException,
			io.minio.errors.MinioException {

		return fileServiceImpl.addFile(file);
	}

	@GetMapping("/{object}")
	public void getObject(@PathVariable("object") String object, HttpServletResponse response)
			throws MinioException, IOException {
		InputStream inputStream = minioService.get(Path.of(object));
		InputStreamResource inputStreamResource = new InputStreamResource(inputStream);

		// Set the content type and attachment header.
		response.addHeader("Content-disposition", "attachment;filename=" + object);
		response.setContentType(URLConnection.guessContentTypeFromName(object));

		// Copy the stream to the response's output stream.
		IOUtils.copy(inputStream, response.getOutputStream());
		response.flushBuffer();
	}

	@GetMapping("/getAllUrl")
	public List<GeneratedUrls> getAllUrl() throws InvalidKeyException, NoSuchAlgorithmException, IOException,
			XmlPullParserException, io.minio.errors.MinioException {
		return fileServiceImpl.getListUrls();
	}

	@GetMapping("/getSingleUrl/{object}")
	public String getUrl(@PathVariable("object") String object) throws InvalidKeyException, NoSuchAlgorithmException,
			IOException, XmlPullParserException, io.minio.errors.MinioException {
		return fileServiceImpl.getSingleUrl(object);
	}

	@DeleteMapping("/deleteSingleFile/{object}")
	public String deleteSingleFile(@PathVariable("object") String object) throws InvalidKeyException,
			NoSuchAlgorithmException, IOException, XmlPullParserException, io.minio.errors.MinioException {

		fileServiceImpl.removeSingleFile(object);

		return "Object delete Succssfully";
	}

	@GetMapping("/getDownloadUrl/{object}")
	public GeneratedUrls getDownloadUrl(@PathVariable("object") String object) throws InvalidKeyException,
			NoSuchAlgorithmException, IOException, XmlPullParserException, io.minio.errors.MinioException {
		return fileServiceImpl.getDownloadUrl(object);
	}

}
