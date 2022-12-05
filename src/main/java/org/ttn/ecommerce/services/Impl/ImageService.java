package org.ttn.ecommerce.services.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.ttn.ecommerce.entity.register.UserEntity;
import org.ttn.ecommerce.exception.UserNotFoundException;
import org.ttn.ecommerce.repository.registerrepository.UserRepository;
import org.ttn.ecommerce.security.SecurityConstants;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ImageService {

    @Autowired
    UserRepository userRepository;

    public String uploadImage(String email, MultipartFile multipartFile) throws IOException {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User Not Found"));
        String[] arr = multipartFile.getContentType().split("/");
        Path uploadPath = Paths.get("/home/rajat/Desktop/image");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(userEntity.getId() + "." + arr[1]);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {
            throw new IOException("Could not save file ", ioe);
        }
        return "Image Uploaded Successfully";
    }

//    public ResponseEntity<?> getImage(String email) {
//
//        String dir = "/home/rajat/Desktop/image";
//
//        UserEntity userEntity = userRepository.findByEmail(email)
//                .orElseThrow(() -> new UserNotFoundException("User Not Found."));
//        File file = new File(dir);
//        for (File files : file.listFiles()) {
//
//            String filesName = files.getName().split("\\.")[0];
//            String fileType = "image/" + files.getName().split("\\.")[1];
//            if (userEntity.getId() == Long.parseLong(filesName)) {
//                byte[] bytes = new byte[(int) files.length()];
//
//                FileInputStream fis = null;
//                try {
//
//                    fis = new FileInputStream(files);
//
//                    fis.read(bytes);
//                    System.out.println(bytes);
//                } catch (Exception e) {
//                    System.out.println(e.fillInStackTrace());
//                }
//
//                return ResponseEntity.status(HttpStatus.OK)
//                        .contentType(MediaType.valueOf(fileType))
//                        .body(bytes);
//            }
//        }
//
//        return new ResponseEntity<>("cant display image", HttpStatus.BAD_REQUEST);
//
//    }

    public String getImagePath(UserEntity userEntity)  {

        String fileName = "";
        try {
            Set<String> fileList = listFilesUsingJavaIO(SecurityConstants.FILE_UPLOAD_URL);
            for (String file : fileList) {
                if (file.startsWith(userEntity.getId().toString())) {
                    fileName = file;
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return SecurityConstants.FILE_UPLOAD_URL+"/"+fileName;
    }


    public Set<String> listFilesUsingJavaIO(String dir) {
        return Stream.of(new File(dir).listFiles())
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toSet());
    }


}