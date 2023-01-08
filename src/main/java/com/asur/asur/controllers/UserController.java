package com.asur.asur.controllers;

import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.asur.asur.models.FileMeta;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@Controller
public class UserController {

    private final static Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private String getRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "admin";
        }
        return "user";
    }

    private String getUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    @GetMapping({ "/index", "/" })
    public String index(Model model, @RequestParam(name = "file", required = false) String filename) {
        model.addAttribute("file", filename);
        model.addAttribute("role", getRole());
        model.addAttribute("username", getUsername());
        // model.addAttribute("validScenario", false);
        return "main";
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

    @GetMapping("/diagnostics")
    public String diagnostics(Model model) {

        model.addAttribute("role", getRole());
        model.addAttribute("username", getUsername());
        return "diagnostics";
    }

    @GetMapping("/robot")
    public String robot(Model model) {
        model.addAttribute("role", getRole());
        model.addAttribute("username", getUsername());
        return "robot";
    }

    @PostMapping("/put-file")
    public String putFile(@RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addAttribute("file", "Please select a file to upload.");
            return "redirect:/";
        }
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            // TODO parsing json
            ObjectMapper mapper = new ObjectMapper();
            FileMeta mission = mapper.readValue(file.getInputStream(), FileMeta.class);
            //Path path = Paths.get("/" + fileName); // TODO check dir
            // Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());

         //   LOGGER.info(Paths.get("src/main/resources/scenarious", fileName).toString());
            writer.writeValue(Paths.get("/asur/asur/src/main/resources/scenarious", mission.getName() + ".json").toFile(), mission);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            redirectAttributes.addAttribute("file", "Error while processing file");
            return "redirect:/";
        }

        redirectAttributes.addAttribute("file", file.getOriginalFilename() + " successfuly load");
        return "redirect:/";
    }

}
