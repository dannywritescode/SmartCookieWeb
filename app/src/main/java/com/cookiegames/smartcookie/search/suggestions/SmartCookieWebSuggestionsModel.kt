package com.cookiegames.smartcookie.search.suggestions

import android.app.Activity
import com.cookiegames.smartcookie.R
import com.cookiegames.smartcookie.constant.UTF8
import com.cookiegames.smartcookie.database.SearchSuggestion
import com.cookiegames.smartcookie.extensions.preferredLocale
import com.cookiegames.smartcookie.log.Logger
import android.app.Application
import android.util.Log
import com.cookiegames.smartcookie.extensions.map
import com.cookiegames.smartcookie.preference.UserPreferences
import io.reactivex.Single
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory

/**
 * Search suggestions provider for Google search engine.
 */
class SmartCookieWebSuggestionsModel(
    okHttpClient: Single<OkHttpClient>,
    requestFactory: RequestFactory,
    application: Application,
    logger: Logger,
    userPreferences: UserPreferences
) : BaseSuggestionsModel(okHttpClient, requestFactory, UTF8, application.preferredLocale, logger, userPreferences) {

    private val searchSubtitle = application.getString(R.string.suggestion)

    // https://smartcookieweb.com/autocomplete.php?query={query}
    override fun createQueryUrl(query: String, language: String): HttpUrl = HttpUrl.Builder()
            .scheme("https")
            .host("smartcookieweb.com")
            .encodedPath("/autocomplete.php")
            .addEncodedQueryParameter("query", query)
            .build()

    @Throws(Exception::class)
    override fun parseResults(responseBody: ResponseBody): List<SearchSuggestion> {
        return JSONObject(responseBody.string())
                .getJSONArray("results")
                .getJSONArray(0)
                .map { it as JSONArray }
                .map { it[0] as String }
                .map { SearchSuggestion("$searchSubtitle \"$it\"", it) }
    }

}
