package zotova_tv.apod.ui.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import zotova_tv.apod.R
import zotova_tv.apod.databinding.NotesFragmentBinding
import zotova_tv.apod.domain.Note
import zotova_tv.apod.ui.util.hide
import zotova_tv.apod.ui.util.hideKeyboard
import zotova_tv.apod.ui.util.show
import zotova_tv.apod.ui.util.toast

class NotesFragment : Fragment() {
    private var _binding: NotesFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var adapter: NotesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = NotesFragmentBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = NotesAdapter(
            object : NotesAdapter.NoteEditButtonClickListener {
                override fun onClick(note: Note, position: Int) {
                    openNoteEditBottomSheet(view, note, position)
                }

            }
        ).apply {
            setNotes(
                listOf(
                    Note("Title", "Text"),
                    Note("Title2", "Text2"),
                    Note("Title3", "Text3")
                )
            )
        }
        ItemTouchHelper(ItemTouchHelperCallback(adapter)).attachToRecyclerView(binding.notesRecyclerView)
        binding.notesRecyclerView.adapter = adapter

        setBottomSheetBehavior(binding.noteAddBottomSheetInclude.roomAddBottomSheetContainer)
        with(binding.noteAddBottomSheetInclude) {
            noteAddButton.setOnClickListener {
                openNoteEditBottomSheet(view)
            }
        }
    }

    private fun updateNote(position: Int, note: Note) {
        adapter.updateNote(position, note)
    }

    private fun insertNote(position: Int, note: Note) {
        adapter.insertNote(position, note)
    }

    private fun setBottomSheetBehavior(bottomSheet: ConstraintLayout) {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> binding.noteAddBottomSheetInclude.noteAddButton.show()
                    BottomSheetBehavior.STATE_EXPANDED -> binding.noteAddBottomSheetInclude.noteAddButton.hide()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        }

        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)
    }

    private fun openNoteEditBottomSheet(
        view: View,
        note: Note? = null,
        position: Int? = null
    ) {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        with(binding.noteAddBottomSheetInclude) {
            if (note != null && position != null) {
                noteTitleEdit.setText(note.title)
                noteTextEdit.setText(note.text)
            }
            noteSaveButton.setOnClickListener {
                val title = noteTitleEdit.text.toString()
                val text = noteTextEdit.text.toString()
                if (title == EMPTY_STRING || text == EMPTY_STRING) {
                    toast(getString(R.string.fill_in_all_fields))
                } else {
                    hideKeyboard(view)
                    closeNoteEditBottomSheet()
                    position?.let {
                        updateNote(position, Note(title, text))
                    } ?: run {
                        insertNote(0, Note(title, text))
                    }
                }
            }
        }
    }

    private fun closeNoteEditBottomSheet() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        with(binding.noteAddBottomSheetInclude) {
            noteTitleEdit.setText(EMPTY_STRING)
            noteTitleEdit.clearFocus()
            noteTextEdit.setText(EMPTY_STRING)
            noteTextEdit.clearFocus()
            noteSaveButton.setOnClickListener(null)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val EMPTY_STRING = ""
    }
}