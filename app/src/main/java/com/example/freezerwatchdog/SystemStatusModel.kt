package com.example.freezerwatchdog

import androidx.annotation.Keep

@Keep
class SystemStatusModel {
    var freezer_id: String? = null
    var status: Boolean? = null

    override fun toString(): String = "{freezer_id: ${freezer_id}, status: ${status}}"
}