package com.zaurh.bober.screen.profile

import com.zaurh.bober.data.report.Reason


data class ReportState(
    val reportState: Boolean = false,
    val reasonDropdown: Boolean = false,
    val selectedReason: Reason = Reason.NOT_SELECTED,
    val optionalReason: String = "",
    val loading: Boolean = false,
    val error: String = ""
)
