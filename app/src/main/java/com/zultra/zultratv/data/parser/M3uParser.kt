package com.zultra.zultratv.data.parser

import com.zultra.zultratv.data.model.Channel
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

object M3uParser {
    fun parse(inputStream: InputStream): List<Channel> {
        val channels = mutableListOf<Channel>()
        val reader = BufferedReader(InputStreamReader(inputStream))
        var line: String?
        var currentName = ""
        var currentLogo: String? = null
        var currentCategory = "General"
        var currentEpgId: String? = null

        val logoRegex = """tvg-logo="([^"]+)"""".toRegex()
        val groupRegex = """group-title="([^"]+)"""".toRegex()
        val epgRegex = """tvg-id="([^"]+)"""".toRegex()

        while (reader.readLine().also { line = it } != null) {
            val trimmed = line!!.trim()
            if (trimmed.startsWith("#EXTINF:")) {
                currentLogo = logoRegex.find(trimmed)?.groupValues?.get(1)
                currentCategory = groupRegex.find(trimmed)?.groupValues?.get(1) ?: "General"
                currentEpgId = epgRegex.find(trimmed)?.groupValues?.get(1)
                
                // Channel name after the last comma
                val commaIndex = trimmed.lastIndexOf(',')
                currentName = if (commaIndex != -1 && commaIndex < trimmed.length - 1) {
                    trimmed.substring(commaIndex + 1).trim()
                } else {
                    "Channel ${channels.size + 1}"
                }
            } else if (trimmed.isNotEmpty() && !trimmed.startsWith("#")) {
                // Stream URL line
                if (currentName.isNotEmpty()) {
                    channels.add(
                        Channel(
                            id = channels.size.toString(),
                            name = currentName,
                            logoUrl = currentLogo,
                            streamUrl = trimmed,
                            category = currentCategory,
                            epgId = currentEpgId
                        )
                    )
                    currentName = ""
                    currentLogo = null
                }
            }
        }
        return channels
    }
}
