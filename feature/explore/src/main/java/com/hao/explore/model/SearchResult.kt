package com.hao.explore.model

import com.hao.data.data.model.RemoteTrack
import com.hao.data.model.Campsite
import com.hao.data.model.Hut

sealed class SearchResult {
    abstract val assetId: String
    abstract val name: String

    data class TrackResult(val track: RemoteTrack) : SearchResult() {
        override val assetId: String = track.assetId
        override val name: String = track.name
    }

    data class CampsiteResult(val campsite: Campsite) : SearchResult() {
        override val assetId: String = campsite.assetId
        override val name: String = campsite.name ?: ""
    }

    data class HutResult(val hut: Hut) : SearchResult() {
        override val assetId: String = hut.assetId
        override val name: String = hut.name ?: ""
    }
}
