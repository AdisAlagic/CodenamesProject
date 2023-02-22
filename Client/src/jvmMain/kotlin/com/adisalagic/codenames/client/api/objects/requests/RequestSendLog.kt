import com.adisalagic.codenames.client.api.BaseAPI
import com.adisalagic.codenames.client.api.objects.Event
import com.adisalagic.codenames.client.api.objects.requests.RequestPressWord
import com.google.gson.annotations.SerializedName

data class RequestSendLog(
    @SerializedName("log")
    val log: String,
    @SerializedName("user")
    val user: RequestPressWord.User
): BaseAPI(Event.REQUEST_SEND_LOG)