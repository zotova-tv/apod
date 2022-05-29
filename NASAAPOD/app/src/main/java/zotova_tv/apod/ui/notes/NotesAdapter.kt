package zotova_tv.apod.ui.notes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import zotova_tv.apod.databinding.NoteItemBinding
import zotova_tv.apod.domain.Note

class NotesAdapter(private var notes: List<Note>): RecyclerView.Adapter<NotesAdapter.NoteHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
        val noteBinding = NoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteHolder(noteBinding)
    }

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        holder.bind(notes[position])
    }

    override fun getItemCount(): Int {
        println(notes)
        return notes.size
    }

    inner class NoteHolder(private val noteBinding: NoteItemBinding): RecyclerView.ViewHolder(noteBinding.root){
        fun bind(note: Note) {
            with(noteBinding){
                noteTitle.text = note.title
                noteText.text = note.text
            }
        }
    }
}