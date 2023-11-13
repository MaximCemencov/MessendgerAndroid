import com.example.androidapp.features.websocketUrl
import com.example.androidapp.viewModels.SharedViewModel
import okhttp3.Request

fun createRequest(sharedViewModel: SharedViewModel): Request {

    return Request.Builder()
        .url("$websocketUrl?login=${sharedViewModel.login}&password=${sharedViewModel.password}")
        .build()
}