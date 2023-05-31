package com.planner.planner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.annotation.SessionScope;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static java.nio.file.StandardWatchEventKinds.*;

import org.springframework.web.bind.annotation.CrossOrigin;

@Component
@ComponentScan
public class UserSessionData
{
    @Scheduled(fixedRate = 60000)
    public void deleteInactiveDirectories() {
    String baseDirectoryPath = System.getProperty("user.dir") + "/user_data";
    File baseDirectory = new File(baseDirectoryPath);
    if (baseDirectory.exists()) {
        File[] userDirectories = baseDirectory.listFiles();
        for (File userDirectory : userDirectories) {
            if (isInactive(userDirectory)) {
                deleteDirectory(userDirectory);
            }
        }
    }
}

private boolean isInactive(File directory) {
    long lastModified = directory.lastModified();
    long currentTime = System.currentTimeMillis();
    long oneHourInMillis = 60 * 60 * 1000;
    return (currentTime - lastModified) > oneHourInMillis;
}

private void deleteDirectory(File directory) {
    File[] files = directory.listFiles();
    if (files != null) {
        for (File file : files) {
            if (file.isDirectory()) {
                deleteDirectory(file);
            } else {
                System.out.println("Directory deleted: " + file);
                file.delete();
            }
        }
    }
    directory.delete();
}

public void watchDirectory() {
    try {
        // Create a WatchService
        WatchService watchService = FileSystems.getDefault().newWatchService();

        // Get the directory to watch
        Path directory = Paths.get(System.getProperty("user.dir") + "/user_data");

        // Register the directory and its subdirectories with the WatchService
        registerAll(directory, watchService);

        // Process events
        while (true) {
            WatchKey key = watchService.take();
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();
                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }
                WatchEvent<Path> ev = (WatchEvent<Path>)event;
                Path filename = ev.context();

                // Call the processNewFile function when a new file is added
                if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                    processNewFile(filename);
                }
            }
            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
    } catch (IOException | InterruptedException e) {
        e.printStackTrace();
    }
}

private static void registerAll(Path directory, WatchService watchService) throws IOException {
    // Register the directory with the WatchService
    directory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

    // Register all subdirectories with the WatchService
    Files.walk(directory)
        .filter(Files::isDirectory)
        .forEach(path -> {
            try {
                path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
}

// @Autowired
// private SimpMessagingTemplate template;

private void processNewFile(Path file) throws IOException {
    System.out.println("Received request to send data to React. ");
    // Execute the bash command
    String[] cmd = {"/bin/bash", "-c", "echo 'hello from bash'"};
    Process process = null;
    try {
        process = Runtime.getRuntime().exec(cmd);
    } catch (IOException e) {
        e.printStackTrace();
    }
    if (process != null) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        StringBuilder output = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            output.append(line);
        }

        // Get the session ID from the file path
        String sessionId = file.getParent().getFileName().toString();

        // Send the output of the bash command to the frontend
        // template.convertAndSendToUser(sessionId, "/queue/bash-output", output.toString());
    }
}

public void execute(){
    // Create a Spring application context
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(UserSessionData.class);

    // Get an instance of the UserSessionData class from the application context
    UserSessionData userSessionData = context.getBean(UserSessionData.class);
    
    // Call the watchDirectory method
    userSessionData.watchDirectory();

    System.out.println("Inside UserSessionData");

    // Call the watchDirectory method
    userSessionData.watchDirectory();
}

}