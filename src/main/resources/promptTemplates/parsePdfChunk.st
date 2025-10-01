For the provided PDF chunk (text + any visible visual content), do the following and return STRICT JSON only (no explanations, no Markdown, no code fences):

1. Clean the text while preserving meaning and structure.
2. Translate faithfully to English, preserving original intent and emphasis.
3. Analyze visual elements present in the pages and integrate their relevant descriptions into the text when they add context.
4. Extract comprehensive metadata using ONLY the provided categories.

TEXT AND VISUAL PROCESSING GUIDELINES:
- Remove noise while preserving meaning:
  - Delete random OCR artifacts (single letters, meaningless characters)
  - Remove page numbers and technical markers
  - Clean up scanning artifacts and misrecognized symbols
  - Delete repeated or broken words from poor OCR
  - Remove irrelevant technical metadata (e.g., "Page 1 of 235")
- BUT preserve:
  - All meaningful text content
  - Important formatting and structure
  - Actual references and citations
  - Intentional special characters or symbols
- Visuals: briefly describe images only if they add context (e.g., album covers, band photos, instruments, sheet music, logos, venues, historical/promotional materials). Include this description inline in the text where relevant. Do not add extra top-level fields for visuals.
- Output must be an accurate English translation of the cleaned text enriched with concise visual context where applicable.

METADATA EXTRACTION:
1. Extract ALL relevant metadata using ONLY these categories (use keys exactly as given):
{tagCategories}
2. Do NOT invent or infer facts that are not supported by the PDF content.
3. For any category with no values, omit that key entirely.
4. Normalize values:
  - Trim whitespace; keep original capitalization of proper names, but use canonical forms when obvious (e.g., "The Beatles" not "Beatles, The" unless in the source).
  - Years should be 4-digit strings when certain (e.g., "1997").
  - Deduplicate values within each list.

OUTPUT FORMAT (strict JSON schema):
{{
  "text": string,            // the cleaned, translated English text (include concise visual context inline if useful)
  "metadata": {{              // only keys from the provided categories
    string: string[]          // each value must be an array of unique strings
  }}
}}

ADDITIONAL CONSTRAINTS:
- Use only double quotes.
- Ensure the response is valid JSON parsable by standard libraries.
- Do not include nulls, empty strings, or empty arrays. Omit keys instead.
- If no metadata is found, return empty "metadata" object.
- Optimize output text for RAG: keep it concise but complete; remove boilerplate and noise; keep lists and headings as simple text if present.
- If the input is already English and clean, preserve content with minimal changes.

EXAMPLE (illustrative; use actual categories provided above):
{{
  "text": "Exact translated text with any brief, relevant visual context",
  "metadata": {{
    "artists": ["Artist1", "Artist2"],
    "albums": ["Album1", "Album2"],
    "years": ["2019", "2020"]
  }}
}}

IMPORTANT:
- Output STRICT JSON only.
- Preserve exact content in translation to the extent possible while removing noise.
- Do not add any commentary.