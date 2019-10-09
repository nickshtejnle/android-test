package com.example.androidtest

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ViewModel
    private val adapter = Adapter()

    private var actionMode: android.view.ActionMode? = null

    private lateinit var selectionTracker: SelectionTracker<Long>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        swipe_refresh.setOnRefreshListener {
            actionMode?.finish()
            viewModel.refresh()
        }

        viewModel = ViewModelProviders.of(this).get(ViewModel::class.java)

        viewModel.hits.observe(this, Observer { hits ->
            adapter.submitList(hits)
            swipe_refresh.isRefreshing = false
        })


        recycler_view.adapter = adapter
        selectionTracker = SelectionTracker.Builder<Long>(
            "selection",
            recycler_view,
            StableIdKeyProvider(recycler_view),
            HitLookup(recycler_view),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()
        adapter.tracker = selectionTracker

        selectionTracker.addObserver(
            object : SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    val count: Int = selectionTracker.selection?.size() ?: 0
                    if(count == 0) {
                        actionMode?.finish()
                        actionMode = null
                    } else {
                        if (actionMode == null) {
                            actionMode = toolbar?.startActionMode(actionModeCallback)
                        }
                        actionMode?.title = resources.getQuantityString(R.plurals.count_selected, count, count)
                    }
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.unsubscribe()
    }

    private val actionModeCallback = object : android.view.ActionMode.Callback {
        override fun onCreateActionMode(mode: android.view.ActionMode, menu: Menu): Boolean {
            return true
        }

        override fun onPrepareActionMode(mode: android.view.ActionMode, menu: Menu): Boolean {
            return true
        }

        override fun onActionItemClicked(mode: android.view.ActionMode, item: MenuItem): Boolean {
            return false
        }

        override fun onDestroyActionMode(mode: android.view.ActionMode) {
            actionMode = null
            selectionTracker.clearSelection()
        }
    }

}
