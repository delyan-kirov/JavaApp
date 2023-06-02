package com.example.demo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller 
public class SrcController {
    
    public List<String> getMarkdownFilesContents(List<String> titles) {
        String basePath = "data";
        List<String> contents = new ArrayList<>();
        for (String title : titles) {
            String dirPath = Paths.get(basePath, title).toString();
            try (java.util.stream.Stream<Path> paths = Files.walk(Paths.get(dirPath))) {
                Optional<Path> markdownFile = paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".md"))
                    .findFirst();
                if (markdownFile.isPresent()) {
                    String content = new String(Files.readAllBytes(markdownFile.get()));
                    contents.add(content);
                } else {
                    System.out.println("No markdown file found in directory: " + dirPath);
                }
            } catch (IOException e) {
                System.err.println("Error reading files in directory: " + dirPath);
                e.printStackTrace();
            }
        }
        return contents;
    }

    @Autowired
    private DataFiles dataFiles;

    @GetMapping("/init")
    @ResponseBody
    public Map<String, List<?>> root() {
        dataFiles.printAllDataFiles();
        // System.out.println(titles);
        // System.out.println(getMarkdownFilesContents(titles));
        System.out.println(dataFiles.getAllPictures());

        List<String> titles = dataFiles.getAllTitles();
        List<Date> dates = dataFiles.getAllDates();
        List<String> summaries = dataFiles.getAllSummaries();
        List<String> ratings = dataFiles.getAllRatings();

        Map<String, List<?>> response = new HashMap<>();
        response.put("titles", titles);
        response.put("dates", dates);
        response.put("summaries", summaries);
        response.put("ratings", ratings);

        return response;
    }

    
    @GetMapping("/pictures")
    @ResponseBody
    public Map<String, List<?>> getPictures() {
        List<String> titles = dataFiles.getAllTitles();
        List<String> pictures = dataFiles.getAllPictures();
    
        Map<String, List<?>> response = new HashMap<>();
        response.put("titles", titles);
    
        List<byte[]> imageData = new ArrayList<>();
        for (int i = 0; i < titles.size(); i++) {
            String title = titles.get(i);
            String picture = pictures.get(i);
            Path picturePath = Paths.get("data", title, picture);
            try {
                byte[] imageBytes = Files.readAllBytes(picturePath);
                imageData.add(imageBytes);
            } catch (IOException e) {
                System.err.println("Error reading image file: " + picturePath);
                e.printStackTrace();
            }
        }
        response.put("pictures", imageData);
    
        return response;
    }

    @GetMapping("/article")
    public ResponseEntity<Resource> getArticle(@RequestParam String title) throws IOException {
        Path dir = Paths.get("data", title);
        Optional<Path> htmlFile = Files.list(dir)
                .filter(path -> path.toString().endsWith(".html"))
                .findFirst();
        if (htmlFile.isPresent()) {
            Resource resource = new InputStreamResource(Files.newInputStream(htmlFile.get()));
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

