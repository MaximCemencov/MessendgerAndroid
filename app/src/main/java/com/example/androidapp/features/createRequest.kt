import com.example.androidapp.features.websocketUrl
import okhttp3.Request

fun createRequest(): Request {

    return Request.Builder()
        .url(websocketUrl)
        .build()
}