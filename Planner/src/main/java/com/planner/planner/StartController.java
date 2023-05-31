package com.planner.planner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@Controller
public class StartController {

    private static final Logger logger = LoggerFactory.getLogger(StartController.class);

    @Autowired
    private UserSessionData userSessionData;

    @RequestMapping("/")
    @ResponseBody
    public String root() {
        return "Hello, this is the root route!";
    }

    @RequestMapping("/hello")
    @ResponseBody
    public String hello() {
        logger.info("Hello endpoint called on Spring server");
        UserSessionData userSessionData = new UserSessionData();
		userSessionData.execute(); 
        return "Hello from the /hello route!";
    }

    @GetMapping("/userdata")
    public ResponseEntity<String> handleMyEndpoint(@RequestParam String sessionId) {
        // Use the sessionId parameter to identify the user and process their request
        logger.info("The session id was: " + sessionId);
    
        String baseDirectoryPath = System.getProperty("user.dir") + "/user_data";
        File baseDirectory = new File(baseDirectoryPath);
        if (!baseDirectory.exists()) {
            baseDirectory.mkdir();
        }
    
        String userDirectoryPath = baseDirectoryPath + "/" + sessionId;
        File userDirectory = new File(userDirectoryPath);
        if (!userDirectory.exists()) {
            logger.info("A directory was created for: " + sessionId);
            userDirectory.mkdir();
        }
    
        return ResponseEntity.ok("Response from the server");
    }

    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam String sessionId, @RequestParam("file") MultipartFile file) {
        logger.info("Received file upload for session id: " + sessionId);

        // Use the sessionId parameter to identify the user and process their request
        String baseDirectoryPath = System.getProperty("user.dir") + "/user_data";
        File baseDirectory = new File(baseDirectoryPath);
        if (!baseDirectory.exists()) {
            baseDirectory.mkdir();
        }

        String userDirectoryPath = baseDirectoryPath + "/" + sessionId;
        File userDirectory = new File(userDirectoryPath);
        if (!userDirectory.exists()) {
            logger.info("A directory was created for: " + sessionId);
            userDirectory.mkdir();
        }

        // Save the uploaded file to the user's directory
        try {
            String filePath = userDirectoryPath + "/" + file.getOriginalFilename();
            file.transferTo(new File(filePath));
            logger.info("File saved to: " + filePath);
        } catch (Exception e) {
            logger.error("Error saving uploaded file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving uploaded file");
        }

        return ResponseEntity.ok("File uploaded successfully");
    }

}