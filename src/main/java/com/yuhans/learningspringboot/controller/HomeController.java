package com.yuhans.learningspringboot.controller;

import com.yuhans.learningspringboot.domain.Image;
import com.yuhans.learningspringboot.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class HomeController {

    private static final String BASE_PATH = "/images";
    private static final String FILENAME = "{fileName:.+}";

    private final ImageService imageService;

    @Autowired
    public HomeController(ImageService imageService) {
        this.imageService = imageService;
    }

    @RequestMapping(value = "/")
    public String index(Model model, Pageable pageable) {
        final Page<Image> page = imageService.findPage(pageable);
        model.addAttribute("page", page);
        if (page.hasPrevious()) {
            model.addAttribute("prev", pageable.previousOrFirst());
        }
        if (page.hasNext()) {
            model.addAttribute("next", pageable.next());
        }
        return "index";
    }

    @RequestMapping(method = RequestMethod.GET, value = BASE_PATH + "/" + FILENAME + "/raw")
    @ResponseBody
    public ResponseEntity<?> oneRawImage(@PathVariable String fileName) {
        try {
            Resource file = imageService.findOneImage(fileName);
            return ResponseEntity.ok()
                    .contentLength(file.contentLength())
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(new InputStreamResource(file.getInputStream()));
        } catch (IOException e ) {
            return ResponseEntity.badRequest()
                    .body("Couldn't find " + fileName + " => " + e.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = BASE_PATH)
    public String createFile(@RequestParam("file")MultipartFile file,
                                        RedirectAttributes redirectAttributes) {
        try {
            imageService.createImage(file);
            redirectAttributes.addFlashAttribute("flash.message", "You nailed it this time " + file.getOriginalFilename());
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("flash.message",
                    "Failed to upload " + file.getOriginalFilename() + " => " + e.getMessage());
        }
        return "redirect:/";
    }

    @RequestMapping(method = RequestMethod.DELETE, value = BASE_PATH + "/" + FILENAME)
    public String deleteFile(@PathVariable String fileName,
                             RedirectAttributes redirectAttributes) {
        try {
            imageService.deleteImages(fileName);
            redirectAttributes.addFlashAttribute("flash.message", "Successfully deleted " + fileName);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("flash.message",
                    "Failed to delete " + fileName + " => " + e.getMessage());
        }
        return "redirect:/";
    }
}
