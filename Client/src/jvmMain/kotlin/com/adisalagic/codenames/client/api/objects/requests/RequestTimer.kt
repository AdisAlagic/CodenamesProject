package com.adisalagic.codenames.client.api.objects.requests

import com.adisalagic.codenames.client.api.BaseAPI


data class RequestTimer(@Transient val none: Nothing? = null): BaseAPI("request_timer")
