package com.example.androidtest

data class ApiResponse(
    val hits: List<Hit>,
    val nbHits: Int,
    val page: Int,
    val nbPages: Int,
    val hitsPerPage: Int,
    val processingTimeMS: Long,
    val exhaustiveNbHits: Boolean,
    val query: String?,
    val params: String?
)


data class Hit(
    val created_at: String,
    val title: String,
    val url: String,
    val author: String,
    val points: Int,
    val story_text: String?,
    val comment_text: String?,
    val num_comments: Int,
    val story_id: String?,
    val story_title: String?,
    val story_url: String?,
    val parent_id: String?,
    val created_at_i: Long,
    val _tags: List<String>,
    val objectID: String,
    val _highlightResult: HighlightResult
)

data class HighlightResult(
    val title: HighlightResultItem,
    val url: HighlightResultItem,
    val author: HighlightResultItem
)


data class HighlightResultItem(
    val value: String,
    val matchLevel: String,
    val matchedWords: List<String>
)