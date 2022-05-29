package zotova_tv.apod.ui.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import zotova_tv.apod.databinding.NotesFragmentBinding
import zotova_tv.apod.domain.Note

class NotesFragment: Fragment() {
    private var _binding: NotesFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = NotesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val notes = listOf(
            Note("Title1", "Text1 Text1 Text1 Text1 Text1 Text1 Text1 Text1 Text1 Text1 Text1 Text1 Text1 Text1 "),
            Note("Title2", "Text2 Text2 Text2 Text2 Text2 Text2 Text2 Text2 "),
            Note("Title3", "Text3 Text3 Text3 Text3 Text3 Text3 Text3 Text3 Text3 Text3 Text3 "),
            Note("Title4", "Text4 Text4 Text4 Text4 Text4 "),
            Note("Title5", "Text5 "),
        )
        binding.notesRecyclerView.adapter = NotesAdapter(notes)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}