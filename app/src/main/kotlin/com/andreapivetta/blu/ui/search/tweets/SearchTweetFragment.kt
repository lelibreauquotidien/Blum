package com.andreapivetta.blu.ui.search.tweets

import android.os.Bundle
import com.andreapivetta.blu.ui.timeline.TimelineFragment

/**
 * Created by andrea on 25/07/16.
 */
class SearchTweetFragment : TimelineFragment() {

    companion object {
        fun newInstance(query: String): SearchTweetFragment {
            val fragment = SearchTweetFragment()
            val bundle = Bundle()
            bundle.putString(TAG_QUERY, query)
            fragment.arguments = bundle
            return fragment
        }

        val TAG_QUERY = "query"
    }

    override fun getTimelinePresenter() = SearchTweetPresenter(arguments.getString(TAG_QUERY))

}