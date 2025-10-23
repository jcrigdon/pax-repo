package com.example.paxrepo

object PaxConstants {
    object Pin {
        const val DES_DUKPT_KEY = "3"
        const val TDES_DUKPT_KEY = "4"
        const val DUKPT_ENCRYPTION = "1"
        const val ISO9564_0_BLOCK_FORMAT = "0"
        const val PIN_KEY_SLOT = "1"
        const val TITLE = "PIN Entry Screen"
        const val ALLOW_USER_BYPASS_ONLINE_OFFLINE_PIN = "4"
    }
    object Emv {
        const val NO_CVM_OR_SIGNATURE = "08"
        const val ALL_CVM_SUPPORTED = "01"
    }
    const val YES = "Y"
    const val NO = "N"
    const val QUICK_CHIP_MODE = "quickChipMode"
    const val SWIPE_TAP_PRIORITY = "swipeTapPriority"
    const val TAP_FIRST = "T"
    const val CONTACT_RETRY_BEFORE_FALLBACK = "contactRetryBeforeFallback"
}
