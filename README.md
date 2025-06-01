<h1>Code Comprehensibility Scoring Tool</h1>
This tool helps analyze code written in different programming languages by checking how easy it is to understand names used in the code—like class names, method names, variables, and even imports or structural parts. It gives each name a readability score by breaking down words using a dictionary, making it easier to measure how clear and understandable the naming really is.
<h2>Project Overview</h2>
This project was created as part of a thesis aimed at exploring how understandable software really is, using automated static analysis. The main goal is to offer a consistent, language-independent way to evaluate how clear and meaningful the names in a codebase are.
<h2> Key Features</h2>
<ul>
  <li><b>Supports multiple languages:</b> Works seamlessly with Java, Python, C, C++, C#, and JavaScript.</li>
  <li><b>Analyzes code at the entity level:</b> Evaluates the clarity of names used for variables, methods, classes, packages, and more.</li>
  <li><b>Detects naming quality:</b>Identifies whether names are easy to understand or potentially confusing by comparing them against dictionary words.</li>
  <li><b>Handles various naming styles:</b> Recognizes and evaluates different naming conventions like camelCase, PascalCase, snake_case, and kebab-case.</li>
  <li><b>Detailed reporting:</b>Provides both a quick summary for each file and in-depth CSV reports with all the analysis details.</li>
</ul>

<h2>How to Run</h2>

<p>Follow the steps below to run the Comprehensibility Score Analyzer for your project:</p>

<ol>
  <li><strong>Install Java:</strong> Make sure you have Java Development Kit (JDK) version 8 or above installed.</li>
  <li><strong>Clone or Download:</strong> Download or clone the repository to your local machine.</li>
  <li><strong>Prepare Dictionary:</strong> Ensure the <code>dictionary.txt</code> dictionary file is located in the project root directory.
  <br><em>(You can use a custom dictionary file if needed.)</em></li>
  <li><strong>Compile the Java Code:</strong></li>
  <pre><code>javac ComprehensibilityScoreCalculator.java</code></pre>
  <li><strong>Run the Program:</strong></li>
  <pre><code>java ComprehensibilityScoreCalculator</code></pre>
  <li><strong>Choose the Language:</strong> Select one of the supported languages from the menu:
    <ul>
      <li>1 - Java</li>
      <li>2 - C#</li>
      <li>3 - Python</li>
      <li>4 - JavaScript</li>
      <li>5 - C/C++ </li>
    </ul>
  </li>
  <li><strong>Enter the Path:</strong> Provide the full path to your project directory or file when prompted. Windows users can paste the path directly—backslashes will be automatically handled.</li>
  <li><strong>View Output:</strong> Two CSV files will be generated in your project folder:
    <ul>
      <li><code>Language_Detailed_Comprehensibility_Report.csv</code></li>
      <li><code>Language_Summary_Comprehensibility_Report.csv</code></li>
    </ul>
  </li>
</ol>

<p><strong>Platform:</strong> Cross-platform compatible (Windows, macOS, Linux)</p>

  <h2>Output Explained</h2>

  <!-- Summary CSV Table -->
  <h3>1. Summary CSV</h3>
  <table border="1" cellspacing="0" cellpadding="8">
    <thead>
      <tr>
        <th>Class Name with Path</th>
        <th>Comprehensibility Score</th>
      </tr>
    </thead>
    <tbody>
      <tr>
        <td>/path/Example.java</td>
        <td>0.78</td>
      </tr>
      <tr>
        <td>/path/Test.java</td>
        <td>0.56</td>
      </tr>
      <tr>
        <td><strong>Average</strong></td>
        <td><strong>0.67</strong></td>
      </tr>
    </tbody>
  </table>

  <!-- Detailed CSV Table -->
  <h3>2. Detailed CSV</h3>
  <table border="1" cellspacing="0" cellpadding="8">
    <thead>
      <tr>
        <th>File</th>
        <th>Entity Name</th>
        <th>Type</th>
        <th>Score</th>
        <th>Category</th>
      </tr>
    </thead>
    <tbody>
      <tr>
        <td>/path/Example.java</td>
        <td>empName</td>
        <td>Variable</td>
        <td>0.75</td>
        <td>Moderate Readable</td>
      </tr>
      <tr>
        <td></td>
        <td>calculateSalary</td>
        <td>Method</td>
        <td>1.00</td>
        <td>Well Readable</td>
      </tr>
      <tr>
        <td></td>
        <td>x</td>
        <td>Variable</td>
        <td>0.00</td>
        <td>Non Readable</td>
      </tr>
    </tbody>
  </table>
</section>

<section id="scoring-logic">
  <h2>Scoring Logic</h2>

  <p>
    Each entity (class, method, variable, etc.) is broken into individual words based on common programming patterns 
    (camelCase, snake_case, PascalCase, etc.). These words are scored for readability using a predefined dictionary.
  </p>

  <h3>1. Word Scoring</h3>
  <ul>
    <li><strong>Full Match:</strong> Word found in the dictionary (≥ 3 characters) → Score: <strong>1.0</strong></li>
    <li><strong>Partial Match:</strong> Contains substring (3 characters) matching dictionary → Score: <strong>0.5</strong></li>
    <li><strong>No Match:</strong> No matching part( < 3 characters) → Score: <strong>0.0</strong></li>
  </ul>

  <h3>2. Entity Score</h3>
  <p>
    The comprehensibility score for a file is calculated using a weighted average based on how many entities fall 
    into each readability category (well, moderate, non-readable):
  </p>

  <p>
    <strong>Formula:</strong><br>
    <span style="font-family: 'Courier New', monospace;">
     (W₁ × F₁ + W₂ × F₂ + W₃ × F₃) / N × 100%
    </span>
  </p>

  <ul>
    <li><strong>W₁, W₂, W₃:</strong> Weights for Well, Moderate, Non Readable (typically 1.0, 0.5, 0.0)</li>
    <li><strong>F₁, F₂, F₃:</strong> Count of Well, Moderate, Non Readable entities</li>
    <li><strong>N:</strong> Total number of entities</li>
  </ul>

  <h3>3. Readability Score Categories</h3>

| Score Range (%) | Category              | Interpretation                                                                 |
|-----------------|----------------------|---------------------------------------------------------------------------------|
| 90–100%         | Excellent Readability | Highly readable and easy to comprehend; minimal effort required.               |
| 70–89%          | Moderate Readability  | Generally readable with some effort; may include minor complexities.           |
| 50–69%          | Fair Readability      | Readability issues present; understanding may require effort.                  |
| 30–49%          | Poor Readability      | Difficult to understand due to complexity or inconsistency.                    |
| 0–29%           | Very Poor Readability | Extremely hard to read; likely to hinder comprehension and maintenance.        |


</section>


<h2>Use Case</h2>
<p>
  This Comprehensibility Score Analyzer is designed to help researchers, developers, and maintainers assess the readability of codebases across multiple programming languages including Java, Python, C++, C, JavaScript, and C#.
</p>

<h3>Applicable Scenarios:</h3>
<ul>
  <li><strong>Academic Research:</strong> Analyze and compare readability of code in software quality studies or thesis projects.</li>
  <li><strong>Code Review Automation:</strong> Integrate into CI pipelines to highlight poorly named entities in pull requests.</li>
  <li><strong>Codebase Refactoring:</strong> Identify and improve non-readable names for better maintainability.</li>
  <li><strong>Educational Tools:</strong> Help students and new developers understand the importance of clear naming conventions.</li>
</ul>

<h3>Who Can Use It?</h3>
<ul>
  <li>Software Engineering Researchers</li>
  <li>Software Architects and Code Reviewers</li>
  <li>Students working on source code quality</li>
  <li>Instructors building tools for coding best practices</li>
</ul>
