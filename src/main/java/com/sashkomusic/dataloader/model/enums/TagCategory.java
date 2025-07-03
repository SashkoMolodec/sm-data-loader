package com.sashkomusic.dataloader.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum TagCategory {
    // documents related tags
    ARTIST("artist", "Individual performers, bands, or musical groups"),
    ALBUM("album", "Full albums, EPs, singles, and other released collections"),
    SONG("song", "Individual track or composition titles"),
    YEAR("year", "Dates of releases, performances, or relevant events"),
    LOCATION("location", "Geographic places related to music creation or performance"),
    LABEL("label", "Record companies and music publishers"),
    CONTEXT("context", "Reviews, interviews, articles, and other media coverage, describes listening context"),
    INSTRUMENT("instrument", "Musical instruments used or mentioned"),
    PERFORMANCE("performance", "Live shows, concerts, and touring information"),
    CRITIC("critic", "Music journalists, reviewers and critics, reflects professional recognition and historical significance"),
    RATING("rating", "Numerical or qualitative evaluations and scores"),
    RELATED_ARTIST("related_artist", "Musicians with similar style or collaborative history"),
    CULTURAL_REFERENCE("cultural_reference", "References to broader cultural elements and influences"),
    HISTORICAL_CONTEXT("historical_context", "Historical events and periods relevant to the music"),
    TECHNOLOGY("technology", "Recording equipment, instruments and production tools used"),

    // album related tags
    GENRE("genre", "Main category for classifying musical style and its subgenres"),
    MOOD("mood", "Describes emotional coloring and atmosphere of the music"),
    EPOCH("epoch", "Indicates historical period or stylistic era of the music"),
    PRODUCTION("production", "Reflects recording quality, mixing style and production approach"),
    THEMATIC("thematic", "Covers lyrical content, concept and main ideas of the album"),
    STRUCTURAL("structural", "Describes musical composition, tempo and complexity"),
    TARGET_AUDIENCE("target_audience", "Indicates intended audience and music accessibility level"),
    FORMAT("format", "In what format album was released"),

    // track related tags
    BPM("bpm", "Indicates exact tempo of the track in beats per minute"),
    KEY("key", "Defines musical key and scale of the composition"),
    DURATION("duration", "Specifies exact length of the track"),
    ENERGY_LEVEL("energy_level", "Measures dynamic intensity and energy flow throughout the track"),
    DANCEABILITY("danceability", "Indicates how suitable the track is for dancing"),
    LOUDNESS("loudness", "Measures average volume level and dynamic range"),
    POSITION("position", "Shows track's placement and role in album's structure"),
    SIMILARITY("similarity", "Points to other tracks that sound alike or share similar features"),
    TRANSITIONS("transitions", "Indicates how well track can be mixed with others"),
    PEAK_MOMENT("peak_moment", "Describes timing and nature of track's climax or drop"),
    VOCALS_TYPE("vocals_type", "Specifies detailed vocal techniques and patterns used"),
    LOOPS("loops", "Indicates presence and type of repeating musical patterns"),
    MIX_BALANCE("mix_balance", "Describes balance between different frequency ranges"),
    STEM_STRUCTURE("stem_structure", "Shows number and type of separate musical layers"),

    OTHER("other", "Other type of important info");

    public final String name;
    public final String description;

    public final static Map<String, TagCategory> dictionary =
            Arrays.stream(TagCategory.values()).collect(Collectors.toMap(TagCategory::getName, Function.identity()));

    TagCategory(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public static String concatenated(List<TagCategory> tagCategories) {
        return tagCategories.stream().map(category -> category.name + ":" + category.description).collect(Collectors.joining(",\n"));
    }

    public static String concatenatedAll() {
        return concatenated(List.of(TagCategory.values()));
    }

    public static String getAlbumTagOptionsString() {
        return concatenated(getAlbumTagOptions());
    }

    public static String getDocumentTagOptionsString() {
        return concatenated(getDocumentTagOptions());
    }

    public static List<TagCategory> getAlbumTagOptions() {
        return List.of(GENRE, MOOD, TECHNOLOGY, EPOCH, INSTRUMENT, LOCATION, PRODUCTION, THEMATIC, STRUCTURAL, TARGET_AUDIENCE, CRITIC, CONTEXT, HISTORICAL_CONTEXT, ALBUM, OTHER);
    }

    public static List<TagCategory> getDocumentTagOptions() {
        return List.of(ARTIST, ALBUM, SONG, YEAR, GENRE, MOOD, FORMAT, LOCATION, LABEL, CONTEXT, INSTRUMENT, PERFORMANCE, CRITIC, RATING, RELATED_ARTIST, CULTURAL_REFERENCE, HISTORICAL_CONTEXT, TECHNOLOGY, OTHER);
    }

    public static TagCategory findByName(String name) {
        return dictionary.get(name);
    }
}
