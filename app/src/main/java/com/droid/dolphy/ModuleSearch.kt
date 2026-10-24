package com.droid.dolphy

import java.util.Locale

/**
 * Matches a module against every whitespace-separated search token.
 * Tokens may appear in the title, description, or section in any order.
 */
internal fun moduleMatchesQuery(
    title: String,
    description: String,
    section: String,
    query: String,
): Boolean {
    val tokens = query.trim().lowercase(Locale.ROOT).split(Regex("\\s+"))
        .filter { it.isNotEmpty() }
    if (tokens.isEmpty()) return true

    val searchableText = listOf(title, description, section)
        .joinToString(separator = "\n")
        .lowercase(Locale.ROOT)
    return tokens.all(searchableText::contains)
}

/** Keeps the original section order while removing modules and sections that do not match. */
internal fun filterFunctionDestinationSections(
    sections: List<Pair<String, List<FunctionDestination>>>,
    query: String,
): List<Pair<String, List<FunctionDestination>>> {
    if (query.isBlank()) return sections
    return sections.mapNotNull { (section, destinations) ->
        val matches = destinations.filter { destination ->
            moduleMatchesQuery(destination.title, destination.description, section, query)
        }
        if (matches.isEmpty()) null else section to matches
    }
}
