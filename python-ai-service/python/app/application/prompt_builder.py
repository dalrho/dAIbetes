from app.domain.dr_result import DRResult

def build_clinical_guidance_prompt(result: DRResult):

    return f"""
You are assisting a clinician reviewing an AI-generated diabetic retinopathy classification.

The AI model predicted:
- Severity: {result.predicted_class}
- Confidence Score: {result.confidence:.2%}

Class probabilities:
{result.probabilities}

This is NOT a definitive diagnosis.

Provide:
1. Common retinal findings associated with this severity
2. Areas clinicians may inspect carefully
3. Possible lesions commonly associated
4. Recommendation for ophthalmologist review

Avoid:
- definitive diagnosis
- claiming lesions definitely exist
- absolute certainty language

Use medically cautious wording.
"""