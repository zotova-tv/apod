package zotova_tv.apod.ui.picture

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.Typeface.BOLD
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.*
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.transition.TransitionManager
import coil.load
import zotova_tv.apod.R
import zotova_tv.apod.ui.picture.viewmodel.PictureOfTheDayData

import zotova_tv.apod.databinding.PictureOfTheDayFragmentBinding
import zotova_tv.apod.ui.util.show
import zotova_tv.apod.ui.util.hide
import zotova_tv.apod.ui.util.toast
import zotova_tv.apod.ui.picture.viewmodel.PictureOfTheDayViewModel
import java.text.SimpleDateFormat
import java.util.*

class PictureOfTheDayFragment : Fragment() {

    private var _binding: PictureOfTheDayFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PictureOfTheDayViewModel by lazy {
        ViewModelProvider(this)[PictureOfTheDayViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PictureOfTheDayFragmentBinding.inflate(inflater, container, false)

        var date: Date? = null
        arguments?.let{args ->
            args.getString(DATE_TAG)?.also { dateStr ->
                try {
                    date = SimpleDateFormat(getString(R.string.pod_api_date_format), Locale.ENGLISH).parse(dateStr)
                }catch (e: Exception){
                    toast(DATE_ERROR)
                }
            }
        }
        viewModel.getData(date).observe(viewLifecycleOwner) { renderData(it) }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setPODChipsClickListeners()

        binding.hdImage.setOnClickListener { v ->
            TransitionManager.beginDelayedTransition(binding.root)
            v.hide()
        }
    }

    private fun renderData(data: PictureOfTheDayData) {
        when (data) {
            is PictureOfTheDayData.Success -> {
                binding.progress.hide()
                val serverResponseData = data.serverResponseData
                val url = if(serverResponseData.mediaType != MEDIA_TYPE_VIDEO) serverResponseData.url
                    else serverResponseData.thumbnailUrl
                val description = decorateDescription(serverResponseData.explanation ?: EMPTY_STRING)
                val podDate = serverResponseData.date ?: EMPTY_STRING
                val title = serverResponseData.title ?: EMPTY_STRING
                var fullScreenUrl: String? = serverResponseData.hdurl
                if (url.isNullOrEmpty()) {
                    toast(getString(R.string.link_is_empty))
                } else {
                    binding.imageView.load(url)
                    binding.imageDescription.text = description
                    binding.podDate.text = podDate
                    binding.mainToolbar.title = title
                }
                fullScreenUrl?.let {
                    binding.hdImage.load(it)
                    binding.fullScreen.show()
                    binding.fullScreen.setOnClickListener {
                        TransitionManager.beginDelayedTransition(binding.root)
                        binding.hdImage.show()
                        binding.hdImage.scaleType = ImageView.ScaleType.CENTER_CROP
                    }
                }
            }
            is PictureOfTheDayData.Loading -> {
                binding.progress.show()
            }
            is PictureOfTheDayData.Error -> {
                toast(data.error.message)
            }
        }
    }

    private fun resetFullScreenFAB(){
        with(binding.fullScreen){
            hide()
            setOnClickListener(null)
        }
    }

    private fun setPODChipsClickListeners(){
        binding.podToday.setOnClickListener {
            resetFullScreenFAB()
            viewModel.getData().observe(viewLifecycleOwner) { renderData(it) }
        }
        binding.podYesterday.setOnClickListener {
            resetFullScreenFAB()
            val c = Calendar.getInstance()
            c.add(Calendar.DATE, -1)
            viewModel.getData(c.time).observe(viewLifecycleOwner) { renderData(it) }
        }
        binding.podByDate.setOnClickListener {
            resetFullScreenFAB()
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            activity?.let {
                val dpd = DatePickerDialog(it, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    val c = Calendar.getInstance()
                    c.set(year, monthOfYear, dayOfMonth)
                    viewModel.getData(c.time).observe(viewLifecycleOwner) { renderData(it) }
                }, year, month, day).apply {
                    datePicker.maxDate = Calendar.getInstance().timeInMillis
                }
                dpd.show()
            }
        }
    }

    private fun decorateDescription(description: String): SpannableString{
        var spannableDescription = SpannableString(description)
        val firstLetterIndexes = mutableListOf(0)
        firstLetterIndexes.addAll(
            Regex("\\. [A-Z]").findAll(description).toMutableList().map{
                it.range.last
            }
        )
        for(i in 0 until firstLetterIndexes.size){
            spannableDescription.setSpan(
                StyleSpan(BOLD),
                firstLetterIndexes[i], firstLetterIndexes[i] + 1,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            )
            spannableDescription.setSpan(
                RelativeSizeSpan(1.2f),
                firstLetterIndexes[i], firstLetterIndexes[i] + 1,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
            )
            spannableDescription.setSpan(
                ForegroundColorSpan(Color.DKGRAY),
                firstLetterIndexes[i], firstLetterIndexes[i] + 1,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
            )
        }
        return spannableDescription
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {

        const val MEDIA_TYPE_VIDEO = "video"
        const val DATE_TAG = "DATE_TAG"
        const val DATE_ERROR = "Date error"
        const val EMPTY_STRING = ""

        fun newInstance(date: String? = null): PictureOfTheDayFragment{
            val fragment = PictureOfTheDayFragment()
            date?.let {
                fragment.arguments = Bundle().apply {
                    putString(DATE_TAG, it)
                }
            }
            return fragment
        }
    }
}
