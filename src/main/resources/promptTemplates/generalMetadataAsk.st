Your task is to analyze the first few pages of a document and extract general metadata that describes the document's content.
Text chunk is provided or pdf file attached.

INSTRUCTIONS:
1. Analyze only the provided document excerpt (typically the first few pages).
2. Extract ONLY GENERAL metadata that would apply broadly to the entire document.
3. DO NOT include specific entities (like individual artists, band names, album titles, or song names) unless they represent a broader category.
4. Identify general metadata elements such as:
    - Time period covered
    - Publication type (catalog, magazine, journal)
    - Publisher information
    - Target audience
    - Historical context
5. Format the output as a clean JSON object with appropriate key-value pairs. If there are no values for a key, do not include it in response
6. Use arrays for metadata fields that may have multiple values.
7. Keep keys simple and descriptive.
8. All output must be in English regardless of the document's original language.

OUTPUT FORMAT:
Provide a JSON object with relevant metadata fields. Example:
{{
    "years": ["2019", "2020"],
    "geography": ["Country1", "City1"],
    "publication_type": ["catalog"],
    "publication_information": ["catalog"],
    "target_audience": ["Reference1"],
    "historical_context": ["Brief historical context"]
  }}

Include only fields that can be confidently determined from the document. If a field cannot be determined, omit it entirely rather than guessing. Remember to keep all metadata GENERAL, not specific to individual artists or works.

TEXT CHUNK (if empty then use pdf attached resource): {text}