package com.beton408.controller;

import com.beton408.entity.UserEntity;
import com.beton408.model.FileInfo;
import com.beton408.model.MessageResponse;
import com.beton408.repository.UserRepository;
import com.beton408.service.FilesStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;
@Controller
@CrossOrigin(value = "*")
public class FilesController {

    @Autowired
    FilesStorageService storageService;
    @Autowired
    private UserRepository userRepository;

    //  @PostMapping(value="/upload/{username}")
//  public ResponseEntity<MessageResponse> uploadFile(@RequestParam("file") MultipartFile file,
//                                                    @PathVariable  String username) {
//    UserEntity user = userRepository.findByUsername(username);
//    user.setAvatar(file.getOriginalFilename());
//    userRepository.save(user);
//    String message = "";
//    try {
//      storageService.save(file);
//      message = "Uploaded the file successfully: " + file.getOriginalFilename();
//      return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse(message));
//    } catch (Exception e) {
//      message = "Could not upload the file: " + file.getOriginalFilename() + ". Error: " + e.getMessage();
//      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageResponse(message));
//    }
//  }
    @PostMapping(value = "/upload/{username}")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                                      @PathVariable String username) {
        UserEntity user = userRepository.findByUsername(username);
        String oldFile = user.getAvatar();
        String fileExtension = getFileExtension(file.getOriginalFilename());
        String randomFileName = UUID.randomUUID().toString() + fileExtension;
        user.setAvatar(randomFileName);
        // Lấy đường dẫn file cũ

        if (oldFile != null) {
            storageService.delete(oldFile);
        }
        String message = "";
        try {
            storageService.saveRandom(file, randomFileName);
            userRepository.save(user);
            message = "Uploaded the file successfully: " + file.getOriginalFilename();
            return new ResponseEntity<>(user.getAvatar(), HttpStatus.OK);
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + ". Error: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageResponse(message));
        }
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex);
        }
        return "";
    }


    @GetMapping("/files")
    public ResponseEntity<List<FileInfo>> getListFiles() {
        List<FileInfo> fileInfos = storageService.loadAll().map(path -> {
            String filename = path.getFileName().toString();
            String url = MvcUriComponentsBuilder
                    .fromMethodName(FilesController.class, "getFile", path.getFileName().toString()).build().toString();

            return new FileInfo(filename, url);
        }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
    }

    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = storageService.load(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @DeleteMapping("/files/{filename:.+}")
    public ResponseEntity<MessageResponse> deleteFile(@PathVariable String filename) {
        String message = "";

        try {
            boolean existed = storageService.delete(filename);

            if (existed) {
                message = "Delete the file successfully: " + filename;
                return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse(message));
            }

            message = "The file does not exist!";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(message));
        } catch (Exception e) {
            message = "Could not delete the file: " + filename + ". Error: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse(message));
        }
    }
}
