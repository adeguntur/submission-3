package com.ade.kotlin.football

import kotlinx.coroutines.experimental.android.UI
import kotlin.coroutines.experimental.CoroutineContext


open class CCProvider{
    open val main: CoroutineContext by lazy { UI }
}