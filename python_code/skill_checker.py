import re
from collections import Counter
from typing import List, Dict, Set
import numpy as np
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

# Import your mappings
from word_label_mappings import WORD_LABEL_MAPPINGS

class ResumeSkillExtractor:
    """
    A class for extracting skills from job descriptions and resumes,
    and comparing their similarity using cosine similarity.
    """
    def __init__(self, word_mappings: Dict[str, str] = None):
        """
        Initialize the skill extractor with word-label mappings.
        Args:
            word_mappings: Dictionary mapping words to labels (B-SKILL, I-SKILL, etc.)
        """
        self.word_mappings = word_mappings or WORD_LABEL_MAPPINGS
        self.skill_labels = {'B-SKILL', 'I-SKILL', 'B-SOFTSKILL', 'I-SOFTSKILL'}

    def preprocess_text(self, text: str) -> List[str]:
        # Lowercase, remove extra whitespace, and tokenize
        text = text.lower()
        text = re.sub(r'\s+', ' ', text)
        tokens = re.findall(r'\b[\w\-\+\.\/]+\b|[^\w\s]', text)
        return tokens

    def extract_skills_from_tokens(self, tokens: List[str]) -> Dict[str, Set[str]]:
        technical_skills = set()
        soft_skills = set()
        i = 0
        while i < len(tokens):
            token = tokens[i]
            if token in self.word_mappings:
                label = self.word_mappings[token]
                if label in ['B-SKILL', 'B-SOFTSKILL']:
                    skill_tokens = [token]
                    j = i + 1
                    # Collect continuation tokens
                    while j < len(tokens):
                        next_token = tokens[j]
                        if (next_token in self.word_mappings and
                                self.word_mappings[next_token] in ['I-SKILL', 'I-SOFTSKILL']):
                            skill_tokens.append(next_token)
                            j += 1
                        else:
                            break
                    full_skill = ' '.join(skill_tokens)
                    if label == 'B-SKILL':
                        technical_skills.add(full_skill)
                    else:
                        soft_skills.add(full_skill)
                    i = j
                else:
                    i += 1
            else:
                i += 1
        return {
            'technical_skills': technical_skills,
            'soft_skills': soft_skills
        }

    def extract_skills(self, text: str) -> Dict[str, Set[str]]:
        tokens = self.preprocess_text(text)
        return self.extract_skills_from_tokens(tokens)

    def calculate_cosine_similarity(self, skills1: Set[str], skills2: Set[str]) -> float:
        if not skills1 or not skills2:
            return 0.0
        all_skills = list(skills1.union(skills2))
        vec1 = [1 if skill in skills1 else 0 for skill in all_skills]
        vec2 = [1 if skill in skills2 else 0 for skill in all_skills]
        vec1 = np.array(vec1).reshape(1, -1)
        vec2 = np.array(vec2).reshape(1, -1)
        similarity = cosine_similarity(vec1, vec2)[0][0]
        return similarity

    def calculate_text_similarity(self, text1: str, text2: str) -> float:
        vectorizer = TfidfVectorizer(stop_words='english')
        tfidf_matrix = vectorizer.fit_transform([text1, text2])
        similarity = cosine_similarity(tfidf_matrix[0:1], tfidf_matrix[1:2])[0][0]
        return similarity

    def compare_skills(self, resume_text: str, job_description: str) -> Dict:
        resume_skills = self.extract_skills(resume_text)
        job_skills = self.extract_skills(job_description)
        resume_all_skills = resume_skills['technical_skills'].union(resume_skills['soft_skills'])
        job_all_skills = job_skills['technical_skills'].union(job_skills['soft_skills'])
        matched_skills = resume_all_skills.intersection(job_all_skills)
        missing_skills = job_all_skills - resume_all_skills

        technical_similarity = self.calculate_cosine_similarity(
            resume_skills['technical_skills'],
            job_skills['technical_skills']
        )
        soft_skill_similarity = self.calculate_cosine_similarity(
            resume_skills['soft_skills'],
            job_skills['soft_skills']
        )
        overall_similarity = self.calculate_cosine_similarity(
            resume_all_skills,
            job_all_skills
        )
        text_similarity = self.calculate_text_similarity(resume_text, job_description)

        if job_all_skills:
            match_percentage = len(matched_skills) / len(job_all_skills) * 100
        else:
            match_percentage = 0.0

        return {
            'resume_skills': {
                'technical_skills': sorted(resume_skills['technical_skills']),
                'soft_skills': sorted(resume_skills['soft_skills']),
                'total_count': len(resume_all_skills)
            },
            'job_skills': {
                'technical_skills': sorted(job_skills['technical_skills']),
                'soft_skills': sorted(job_skills['soft_skills']),
                'total_count': len(job_all_skills)
            },
            'matched_skills': sorted(matched_skills),
            'missing_skills': sorted(missing_skills),
            'similarity_scores': {
                'technical_skills': round(technical_similarity, 3),
                'soft_skills': round(soft_skill_similarity, 3),
                'overall_skills': round(overall_similarity, 3),
                'text_similarity': round(text_similarity, 3)
            },
            'match_percentage': round(match_percentage, 2)
        }

if __name__ == "__main__":
    extractor = ResumeSkillExtractor()

    sample_resume = """
    John Doe
    Software Engineer
    I am a skilled python developer with experience in machine learning and data analysis.
    Proficient in JavaScript, React, and Node.js for web development.
    Strong problem solving abilities and excellent communication skills.
    Experience with AWS, Docker, and Kubernetes for cloud deployment.
    """

    sample_job_desc = """
    We are looking for a Senior Python Developer with expertise in:
    - Python programming and Django framework
    - Machine learning and data science
    - JavaScript, React.js for frontend development
    - AWS cloud services and Docker containerization
    - Strong analytical thinking and team collaboration skills
    - Experience with PostgreSQL and API development
    """

    results = extractor.compare_skills(sample_resume, sample_job_desc)

    print("=== RESUME SKILLS ===")
    print("Technical skills:", results['resume_skills']['technical_skills'])
    print("Soft skills:", results['resume_skills']['soft_skills'])
    print("Total:", results['resume_skills']['total_count'])

    print("\n=== JOB DESCRIPTION SKILLS ===")
    print("Technical skills:", results['job_skills']['technical_skills'])
    print("Soft skills:", results['job_skills']['soft_skills'])
    print("Total:", results['job_skills']['total_count'])

    print("\n=== MATCHED SKILLS ===")
    print(results['matched_skills'])

    print("\n=== MISSING SKILLS ===")
    print(results['missing_skills'])

    print("\n=== SIMILARITY SCORES ===")
    for cat, val in results['similarity_scores'].items():
        print(f"{cat}: {val}")

    print(f"\nMatch percentage: {results['match_percentage']}%")


