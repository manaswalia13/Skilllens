package com.skillens.resumeanalyzer;

import org.springframework.stereotype.Service;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;
import java.io.IOException;

@Service
public class ResumeAnalysisService {

    /**
     * Extracts text from a given file input stream using Apache Tika
     * and then analyzes the extracted text.
     * @param inputStream The InputStream of the uploaded file.
     * @return An AnalysisResult object containing the score and suggestions.
     */
    public AnalysisResult analyzeResumeFromStream(InputStream inputStream) throws IOException, TikaException, SAXException {
        ContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        ParseContext parseContext = new ParseContext();
        
        AutoDetectParser parser = new AutoDetectParser();
        parser.parse(inputStream, handler, metadata, parseContext);

        String resumeText = handler.toString();
        return analyzeResume(resumeText);
    }
    
    /**
     * Analyzes the provided resume text based on a set of predefined rules.
     * It calculates an ATS score and provides a list of actionable suggestions.
     * @param resumeText The plain text content of the resume.
     * @return An AnalysisResult object containing the score and suggestions.
     */
    public AnalysisResult analyzeResume(String resumeText) {
        double score = 0;
        final List<String> suggestions = new ArrayList<>();
        final String resumeContent = resumeText.toLowerCase();

        final Set<String> addedSuggestions = new HashSet<>();
        double bonusScore = 0;

        // Rule-based scoring and suggestions
        // Scoring for essential sections
        if (resumeContent.contains("contact")) {
            score += 10;
        } else {
            addSuggestion(suggestions, addedSuggestions, "Add a clear 'Contact Information' section at the top.");
        }
        if (resumeContent.contains("summary")) {
            score += 15;
        } else {
            addSuggestion(suggestions, addedSuggestions, "Add a 'Professional Summary' or 'Objective' section to quickly state your goals and skills.");
        }
        if (resumeContent.contains("experience")) {
            score += 20;
        } else {
            addSuggestion(suggestions, addedSuggestions, "A 'Work Experience' section is critical. Make sure it highlights your accomplishments, not just duties.");
        }
        if (resumeContent.contains("skills")) {
            score += 20;
        } else {
            addSuggestion(suggestions, addedSuggestions, "Include a dedicated 'Skills' section to list your technical and soft skills.");
        }
        if (resumeContent.contains("education")) {
            score += 10;
        } else {
            addSuggestion(suggestions, addedSuggestions, "Make sure you have an 'Education' section with your degree and institution.");
        }

        // Scoring for formatting and common mistakes
        if (resumeContent.contains(".png") || resumeContent.contains(".jpg")) {
            score -= 10;
            addSuggestion(suggestions, addedSuggestions, "Remove your photo. Most ATS systems cannot process images and they take up valuable space.");
        }
        
        // Bonus for strong action verbs and keywords (example)
        if (resumeContent.contains("developed")) {
            bonusScore += 2;
            addSuggestion(suggestions, addedSuggestions, "Use strong action verbs to start bullet points.");
        }
        if (resumeContent.contains("managed")) {
            bonusScore += 2;
            addSuggestion(suggestions, addedSuggestions, "Use strong action verbs to start bullet points.");
        }
        if (resumeContent.contains("created")) {
            bonusScore += 2;
            addSuggestion(suggestions, addedSuggestions, "Use strong action verbs to start bullet points.");
        }
        if (resumeContent.contains("javascript")) {
            bonusScore += 3;
            addSuggestion(suggestions, addedSuggestions, "Incorporate more industry-specific keywords.");
        }
        if (resumeContent.contains("python")) {
            bonusScore += 3;
            addSuggestion(suggestions, addedSuggestions, "Incorporate more industry-specific keywords.");
        }
        if (resumeContent.contains("react")) {
            bonusScore += 3;
            addSuggestion(suggestions, addedSuggestions, "Incorporate more industry-specific keywords.");
        }
        if (resumeContent.contains("java")) {
            bonusScore += 3;
            addSuggestion(suggestions, addedSuggestions, "Incorporate more industry-specific keywords.");
        }
        
        // Cap the bonus score to prevent it from inflating the total too much
        score += Math.min(bonusScore, 10);
        
        // Final score calculation
        final int finalScore = (int) Math.max(0, Math.min(100, Math.round(score)));

        return new AnalysisResult(finalScore, new ArrayList<>(suggestions));
    }

    private void addSuggestion(List<String> suggestions, Set<String> addedSuggestions, String suggestion) {
        if (!addedSuggestions.contains(suggestion)) {
            suggestions.add(suggestion);
            addedSuggestions.add(suggestion);
        }
    }
}

// Data Transfer Object for the response body
class AnalysisResult {
    private int score;
    private List<String> suggestions;

    public AnalysisResult(int score, List<String> suggestions) {
        this.score = score;
        this.suggestions = suggestions;
    }

    public int getScore() {
        return score;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }
}
