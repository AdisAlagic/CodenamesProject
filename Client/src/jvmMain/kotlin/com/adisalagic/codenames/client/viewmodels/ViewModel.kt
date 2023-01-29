package com.adisalagic.codenames.client.viewmodels

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

open class ViewModel {
    protected val viewModelScope: CoroutineScope = CoroutineScope(Job())

}