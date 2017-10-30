package com.yuhans.learningspringboot.service;

import com.yuhans.learningspringboot.domain.Image;
import com.yuhans.learningspringboot.domain.User;
import com.yuhans.learningspringboot.repository.ImageRepository;
import com.yuhans.learningspringboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.actuate.metrics.repository.InMemoryMetricRepository;
import org.springframework.boot.actuate.metrics.writer.Delta;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class ImageService {

    private static final String UPLOAD_ROOT = "upload-dir";

    private final ImageRepository repository;
    private final ResourceLoader resourceLoader;
    private final CounterService counterService;
    private final GaugeService gaugeService;
    private final InMemoryMetricRepository inMemoryMetricRepository;

    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository,
                        ResourceLoader resourceLoader,
                        @Qualifier("counterService") CounterService counterService,
                        @Qualifier("gaugeService") GaugeService gaugeService,
                        InMemoryMetricRepository inMemoryMetricRepository,
                        SimpMessagingTemplate messagingTemplate,
                        UserRepository userRepository) {

        this.repository = imageRepository;
        this.resourceLoader = resourceLoader;
        this.counterService = counterService;
        this.gaugeService = gaugeService;
        this.inMemoryMetricRepository = inMemoryMetricRepository;
        this.messagingTemplate = messagingTemplate;
        this.userRepository = userRepository;

        this.counterService.reset("files.uploaded");
        this.gaugeService.submit("files.uploaded.lastBytes", 0);
        this.inMemoryMetricRepository.set(new Metric<Number>("files.uploaded.totalBytes", 0));
    }

    public Page<Image> findPage(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Resource findOneImage(String fileName) {
        return resourceLoader.getResource("file:" + UPLOAD_ROOT + "/" + fileName);
    }

    public void createImage(MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            Files.copy(file.getInputStream(), Paths.get(UPLOAD_ROOT, file.getOriginalFilename()));
            repository.save(new Image(
                    file.getOriginalFilename(),
                    userRepository.findByUserName(SecurityContextHolder.getContext().getAuthentication().getName())));
            counterService.increment("files.uploaded");
            gaugeService.submit("files.uploaded.lastBytes", file.getSize());
            inMemoryMetricRepository.increment(new Delta<Number>("files.uploaded.totalBytes", file.getSize()));
            messagingTemplate.convertAndSend("/topic/newImage", file.getOriginalFilename());
        }
    }

    public void deleteImages(String fileName) throws IOException {
        final Image byName = repository.findByName(fileName);
        repository.delete(byName);
        Files.deleteIfExists(Paths.get(UPLOAD_ROOT, fileName));
        messagingTemplate.convertAndSend("/topic/deleteImage", fileName);
    }

    @Bean
    CommandLineRunner setUp(ImageRepository imageRepository,
                            UserRepository userRepository) throws IOException {
        return args -> {
            FileSystemUtils.deleteRecursively(new File(UPLOAD_ROOT));

            Files.createDirectory(Paths.get(UPLOAD_ROOT));

            User yuhans = userRepository.save(new User("yuhans", "password", new String[]{"ROLE_ADMIN", "ROLE_USER"}));
            User guest = userRepository.save(new User("guest", "password", new String[]{"ROLE_USER"}));

            FileCopyUtils.copy("Test file", new FileWriter(UPLOAD_ROOT + "/test"));
            imageRepository.save(new Image("test", yuhans));

            FileCopyUtils.copy("Test file2", new FileWriter(UPLOAD_ROOT + "/test2"));
            imageRepository.save(new Image("test2", yuhans));

            FileCopyUtils.copy("Test file3", new FileWriter(UPLOAD_ROOT + "/test3"));
            imageRepository.save(new Image("test3", guest));
        };
    }
}
