package com.example.braillink

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class ScreenTextAccessibilityService : AccessibilityService() {

    companion object {
        private var latestText: String = ""

        fun getLatestText(): String {
            return latestText
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED ||
            event?.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED ||
            event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {

            val rootNode = rootInActiveWindow ?: return
            val collected = StringBuilder()
            collectTextFromNode(rootNode, collected)

            if (collected.isNotEmpty()) {
                latestText = collected.toString().trim()
            }
        }
    }

    override fun onInterrupt() {
        // not needed
    }

    private fun collectTextFromNode(node: AccessibilityNodeInfo?, sb: StringBuilder) {
        if (node == null) return

        if (node.text != null) {
            sb.append(node.text.toString()).append(" ")
        }

        for (i in 0 until node.childCount) {
            collectTextFromNode(node.getChild(i), sb)
        }
    }
}
