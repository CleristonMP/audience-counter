package com.cmp.audiencecounter.model

data class AudienceRecord(
    val timestampMillis: Long,
    val count: Int
) {
    init {
        require(timestampMillis >= 0) { "Timestamp cannot be negative" }
        require(count in 1..MAX_AUDIENCE_COUNT) { "Audience count is out of range" }
    }
}

const val MAX_AUDIENCE_COUNT = Int.MAX_VALUE
const val MAX_ROW_COUNT = 10_000
