import com.adisalagic.codenames.client.api.BaseAPI
import com.adisalagic.codenames.client.api.objects.Event
import com.google.gson.annotations.SerializedName

data class PlayerInfo(
    @SerializedName("user")
    val user: User
): BaseAPI(Event.GAME_PLAYER_INFO) {
    data class User(
        @SerializedName("color")
        val color: Long, //FFFFFFFF ARGB
        @SerializedName("id")
        val id: Int,
        @SerializedName("isHost")
        val isHost: Boolean,
        @SerializedName("nickname")
        val nickname: String,
        @SerializedName("role")
        val role: Int,
        @SerializedName("team")
        val team: Int
    )
}