package com.zaurh.bober.data.report

enum class Reason(val displayName: String) {
    NOT_SELECTED(displayName = "Not selected"),
    FAKE(displayName = "Fake or scammer"),
    NOT_LEGAL_AGE(displayName = "User is under 18"),
    NUDITY(displayName = "Nudity or something explicit"),
    ABUSIVE(displayName = "Abusive or hateful behaviour")
}