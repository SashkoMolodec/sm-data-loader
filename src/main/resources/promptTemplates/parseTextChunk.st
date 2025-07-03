For the provided text chunk, please:
1. Clean and preserve the exact text content
2. Translate the content faithfully to English
3. Extract comprehensive metadata specific to this text
4. Response should be in json, do not add anything additional as it may break parsing

TEXT PROCESSING GUIDELINES:
- Remove noise while preserving meaning:
  * Delete random OCR artifacts (single letters, meaningless characters)
  * Remove page numbers and technical markers
  * Clean up scanning artifacts and misrecognized symbols
  * Delete repeated or broken words from poor OCR
  * Remove irrelevant technical metadata (e.g., "Page 1 of 235")
- BUT preserve:
  * All meaningful text content
  * Important formatting and structure
  * Actual references and citations
  * Intentional special characters or symbols
- Provide accurate English translation

METADATA EXTRACTION:
1. Extract ALL relevant metadata, use this categories:
{tagCategories}
2. Format the output as a clean JSON object with appropriate key-value pairs. If there are no values for a key, do not include it in response

OUTPUT FORMAT:
1. Provide a JSON object with relevant metadata fields.
2. Use only double quotes.
3. Each value next to key in metadata should be list.
An example (but use metadata keys from categories mentioned before):
{{
  "text": "Exact translated text",
  "metadata": {{
    "artists": ["Artist1", "Artist2"],
    "albums": ["Album1", "Album2"],
    "years": ["2019", "2020"]
}}

IMPORTANT NOTES:
1. Preserve EXACT text content in translation
2. Do not omit any information but remove noise
3. Format your response as a valid JSON string


TEXT CHUNK: {text}