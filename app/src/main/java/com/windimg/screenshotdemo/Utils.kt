package com.windimg.screenshotdemo

object Utils {

    private val KEYWORDS = arrayOf(
        "screenshot",
        "screen_shot",
        "screen-shot",
        "screen shot",
        "screencapture",
        "screen_capture",
        "screen-capture",
        "screen capture",
        "screencap",
        "screen_cap",
        "screen-cap",
        "screen cap"
    )

    /**
     * 判断是否是截屏
     */
    fun checkScreenShot(data: String, dateTaken: Long): Boolean {
        var data = data
        data = data.toLowerCase()
        // 判断图片路径是否含有指定的关键字之一, 如果有, 则认为当前截屏了
        for (keyWork in KEYWORDS) {
            if (data.contains(keyWork)) {
                return true
            }
        }
        return false
    }
}