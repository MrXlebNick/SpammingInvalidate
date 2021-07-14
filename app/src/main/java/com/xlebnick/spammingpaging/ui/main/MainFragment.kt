package com.xlebnick.spammingpaging.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.xlebnick.spammingpaging.R
import com.xlebnick.spammingpaging.model.Kitty
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainFragment : Fragment() {


    companion object {
        fun newInstance() = MainFragment()
    }

    private  val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.main_fragment, container, false)
        with(view.findViewById<RecyclerView>(R.id.recyclerView)) {
            val listAdapter = KittiesAdapter()
            adapter = listAdapter

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.kitties.collectLatest {
                    listAdapter.submitData(it)
                }
            }
        }
        view.findViewById<Button>(R.id.message).setOnClickListener {
            viewModel.invalidate()
        }

        return view
    }

}


class KittiesAdapter(
) :
    PagingDataAdapter<Kitty, KittiesAdapter.KittiesViewHolder>(KittyComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KittiesViewHolder {
        return KittiesViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.kitties_list_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: KittiesViewHolder, position: Int) {
        holder.bind(getItem(position) ?: return)
    }

    class KittiesViewHolder(private val view: View) :
        RecyclerView.ViewHolder(view) {
        fun bind(kitty: Kitty) {
            view.findViewById<TextView>(R.id.name).text = kitty.name
        }
    }

    object KittyComparator : DiffUtil.ItemCallback<Kitty>() {
        override fun areItemsTheSame(oldItem: Kitty, newItem: Kitty): Boolean {
            // Id is unique.
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Kitty, newItem: Kitty): Boolean {
            return oldItem == newItem
        }
    }

}