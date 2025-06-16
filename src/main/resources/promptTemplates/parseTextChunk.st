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
1. Extract ALL relevant metadata including:
    - Artists/bands/musicians
    - Albums/EPs/singles
    - Song titles
    - Music genres
    - Years (release dates, performance dates)
    - Geographic locations
    - Record labels
    - Musical context (reviews, interviews, news, etc.)
    - Instruments mentioned
    - Performances/concerts
    - Critics/reviewers
    - Ratings/scores if applicable
    - Related artists
    - Cultural references
    - Historical context
    - Music technology discussed
2. Format the output as a clean JSON object with appropriate key-value pairs. If there are no values for a key, do not include it in response
3. Use arrays for metadata fields that may have multiple values.

OUTPUT FORMAT:
{{
  "text": "Exact translated text",
  "metadata": {{
    "artists": ["Artist1", "Artist2"],
    "albums": ["Album1", "Album2"],
    "songs": ["Song1", "Song2"],
    "genres": ["Genre1", "Genre2"],
    "years": ["2019", "2020"],
    "geography": ["Country1", "City1"],
    "labels": ["Label1"],
    "context_type": ["review", "interview"],
    "instruments": ["Instrument1", "Instrument2"],
    "performances": ["Performance1"],
    "critics": ["Critic1"],
    "related_artists": ["RelatedArtist1"],
    "cultural_references": ["Reference1"],
    "historical_context": ["Brief historical context"]
  }}
}}

IMPORTANT NOTES:
1. Preserve EXACT text content in translation
2. Do not omit any information but remove noise
3. Check if json response is correct

TEXT CHUNK: {text}