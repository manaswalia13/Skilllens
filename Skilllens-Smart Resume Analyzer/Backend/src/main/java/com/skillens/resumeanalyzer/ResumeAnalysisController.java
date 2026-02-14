package com.skillens.resumeanalyzer;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ResumeAnalysisController {

    @Autowired
    private ResumeAnalysisService resumeAnalysisService;

    @PostMapping("/analyze-file")
    public AnalysisResult analyzeResume(@RequestParam("file") MultipartFile file) {
        try {
            return resumeAnalysisService.analyzeResumeFromStream(file.getInputStream());
        } catch (IOException | TikaException | SAXException e) {
            e.printStackTrace();
            return new AnalysisResult(0, List.of("An error occurred while processing the file. Please try a different file format."));
        }
    }

    @PostMapping("/analyze")
    public AnalysisResult analyzeResume(@RequestBody ResumeAnalysisRequest request) {
        return resumeAnalysisService.analyzeResume(request.getResumeText());
    }

}

// Data Transfer Object for the request body
class ResumeAnalysisRequest {
    private String resumeText;

    public String getResumeText() {
        return resumeText;
    }

    public void setResumeText(String resumeText) {
        this.resumeText = resumeText;
    }
}
