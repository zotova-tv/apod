package zotova_tv.apod.ui.notes

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import zotova_tv.apod.databinding.NoteItemBinding
import zotova_tv.apod.domain.Note
import java.util.*

class NotesAdapter(
    val noteEditButtonClickListener: NoteEditButtonClickListener
) : RecyclerView.Adapter<NotesAdapter.NoteHolder>(), ItemTouchHelperAdapter {

    private var notes = mutableListOf<Note>()

    fun setNotes(newNotes: List<Note>) {
        notes.clear()
        notes.addAll(newNotes)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
        val noteBinding =
            NoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteHolder(noteBinding)
    }

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        holder.bind(notes[position], position)
    }

    override fun getItemCount(): Int {
        println(notes)
        return notes.size
    }

    fun insertNote(position: Int, note: Note) {
        notes.add(position, note)
        notifyItemInserted(position)
    }

    fun updateNote(position: Int, note: Note) {
        notes[position] = note
        notifyItemChanged(position)
    }

    inner class NoteHolder(private val noteBinding: NoteItemBinding) :
        RecyclerView.ViewHolder(noteBinding.root),
        ItemTouchHelperViewHolder {
        fun bind(note: Note, position: Int) {
            with(noteBinding) {
                noteTitle.text = note.title
                noteText.text = note.text
                noteEditButton.setOnClickListener {
                    noteEditButtonClickListener.onClick(note, position)
                }
            }
        }

        override fun onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY)
        }

        override fun onItemClear() {
            itemView.setBackgroundColor(0)
        }
    }

    interface NoteEditButtonClickListener {
        fun onClick(note: Note, position: Int)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(notes, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {
        notes.removeAt(position)
        notifyItemRemoved(position)
    }


}