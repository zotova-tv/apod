package zotova_tv.apod.ui.picture.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import zotova_tv.apod.BuildConfig

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import zotova_tv.apod.data.PODRetrofitImpl
import zotova_tv.apod.data.PODServerResponseData
import java.text.SimpleDateFormat
import java.util.*


const val YOU_NEED_API_KEY_ERROR_TEXT = "You need API key"
const val UNIDENTIFIED_ERROR_TEXT = "Unidentified error"
const val POD_API_DATE_FORMAT = "yyyy-MM-dd"

class PictureOfTheDayViewModel(
    private val liveDataForViewToObserve: MutableLiveData<PictureOfTheDayData> = MutableLiveData(),
    private val retrofitImpl: PODRetrofitImpl = PODRetrofitImpl()
) :
    ViewModel() {

    fun getData(date: Date? = null): LiveData<PictureOfTheDayData> {
        sendServerRequest(date)
        return liveDataForViewToObserve
    }

    private fun sendServerRequest(date: Date?) {
        liveDataForViewToObserve.value = PictureOfTheDayData.Loading(null)
        val apiKey: String = BuildConfig.NASA_API_KEY
        if (apiKey.isBlank()) {
            PictureOfTheDayData.Error(Throwable(YOU_NEED_API_KEY_ERROR_TEXT))
        } else {
            var dateStr: String? = null
            date?.let {
                dateStr = SimpleDateFormat(POD_API_DATE_FORMAT, Locale.ENGLISH).format(it)
            }
            retrofitImpl.getRetrofitImpl().getPictureOfTheDay(apiKey, true, dateStr)
                .enqueue(object :
                    Callback<PODServerResponseData> {
                    override fun onResponse(
                        call: Call<PODServerResponseData>,
                        response: Response<PODServerResponseData>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            response.body()?.let {
                                liveDataForViewToObserve.value = PictureOfTheDayData.Success(it)
                            }
                        } else {
                            val message = response.message()
                            if (message.isNullOrEmpty()) {
                                liveDataForViewToObserve.value =
                                    PictureOfTheDayData.Error(Throwable(UNIDENTIFIED_ERROR_TEXT))
                            } else {
                                liveDataForViewToObserve.value =
                                    PictureOfTheDayData.Error(Throwable(message))
                            }
                        }
                    }

                    override fun onFailure(call: Call<PODServerResponseData>, t: Throwable) {
                        liveDataForViewToObserve.value = PictureOfTheDayData.Error(t)
                    }
                })
        }
    }
}
