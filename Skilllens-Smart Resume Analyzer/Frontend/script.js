const fileInput = document.getElementById('file-upload');
const analyzeButton = document.getElementById('analyze-button');
const resumeTextArea = document.getElementById('resume-text-area');
const uploadSection = document.getElementById('upload-section');
const loadingSpinner = document.getElementById('loading-spinner');
const resultsSection = document.getElementById('results-section');
const scoreText = document.getElementById('score-text');
const scoreCircle = document.getElementById('score-circle');
const suggestionsList = document.getElementById('suggestions-list');
const analyzeAgainBtn = document.getElementById('analyze-again-btn');

// Event listeners
fileInput.addEventListener('change', handleFileUpload);
analyzeButton.addEventListener('click', handleAnalyzeButtonClick);
analyzeAgainBtn.addEventListener('click', resetUI);

function handleAnalyzeButtonClick() {
    const resumeText = resumeTextArea.value.trim();
    if (resumeText.length > 0) {
        runAnalysis(resumeText);
    } else {
        showMessage("Please upload a file or paste your resume content.");
    }
}

function handleFileUpload() {
    const file = fileInput.files[0];
    if (!file) return;

    // Simple validation for text files
    if (file.type !== 'text/plain') {
        showMessage("Please upload a plain text (.txt) file.");
        fileInput.value = ''; // Clear the file input
        return;
    }

    const reader = new FileReader();
    reader.onload = function(e) {
        const resumeText = e.target.result;
        resumeTextArea.value = resumeText;
        runAnalysis(resumeText);
    };
    reader.readAsText(file);
}

async function runAnalysis(resumeText) {
    // Show loading state
    uploadSection.classList.add('hidden');
    loadingSpinner.classList.remove('hidden');

    try {
        // Send the resume text to our Java backend
        const response = await fetch('http://localhost:8080/api/analyze', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ resumeText: resumeText }),
        });

        if (!response.ok) {
            throw new Error('Network response was not ok');
        }

        const analysis = await response.json();
        displayResults(analysis);
    } catch (error) {
        console.error('Error during analysis:', error);
        showMessage("An error occurred during analysis. Please ensure your backend is running.");
        resetUI();
    }
}

function displayResults(analysis) {
    loadingSpinner.classList.add('hidden');
    resultsSection.classList.remove('hidden');

    const score = analysis.score;
    const circumference = 2 * Math.PI * 60;
    const dashoffset = circumference * (1 - score / 100);

    // Animate the score from 0 to the final score
    let currentScore = 0;
    const scoreInterval = setInterval(() => {
        if (currentScore < score) {
            currentScore++;
            scoreText.textContent = currentScore;
        } else {
            clearInterval(scoreInterval);
        }
    }, 10);

    // Animate the circle
    scoreCircle.style.strokeDashoffset = dashoffset;

    // Populate suggestions
    suggestionsList.innerHTML = '';
    if (analysis.suggestions && analysis.suggestions.length > 0) {
        analysis.suggestions.forEach(suggestion => {
            const li = document.createElement('li');
            li.classList.add('flex', 'items-start', 'text-gray-700', 'dark:text-gray-300');
            li.innerHTML = `
                <svg class="flex-shrink-0 w-5 h-5 mt-1 mr-2 text-indigo-500" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm-1-8a1 1 0 011-1h.01a1 1 0 110 2H10a1 1 0 01-1-1zm2 3a1 1 0 11-2 0 1 1 0 012 0z" clip-rule="evenodd" />
                </svg>
                <span>${suggestion}</span>
            `;
            suggestionsList.appendChild(li);
        });
    } else {
         const li = document.createElement('li');
         li.textContent = "Your resume looks great! No major suggestions at this time.";
         suggestionsList.appendChild(li);
    }
}

function showMessage(message) {
     const messageDiv = document.createElement('div');
     messageDiv.classList.add('mt-4', 'p-4', 'rounded-lg', 'bg-red-100', 'text-red-700', 'dark:bg-red-900', 'dark:text-red-300');
     messageDiv.textContent = message;
     document.body.appendChild(messageDiv);
     setTimeout(() => messageDiv.remove(), 5000);
}

function resetUI() {
    uploadSection.classList.remove('hidden');
    loadingSpinner.classList.add('hidden');
    resultsSection.classList.add('hidden');
    scoreText.textContent = '0';
    scoreCircle.style.strokeDashoffset = '376.99';
    suggestionsList.innerHTML = '';
    fileInput.value = ''; // Clear file input
    resumeTextArea.value = ''; // Clear text area
}
