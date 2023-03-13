package com.example.hloomap.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar


fun RecyclerView.setupRecyclerView(
    layoutManager: RecyclerView.LayoutManager,
    adapter: RecyclerView.Adapter<*>
) {
    this.layoutManager = layoutManager
    this.setHasFixedSize(true)
    this.isNestedScrollingEnabled = false
    this.adapter = adapter
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun RecyclerView.initRecyclerView(
    layoutManager: RecyclerView.LayoutManager,
    adapter: RecyclerView.Adapter<*>
) {
    this.layoutManager = layoutManager
    this.adapter = adapter
//    this.setHasFixedSize(true)
}

fun Snackbar.setMessage(message: String) {
//      Snackbar.make(view, message,
//         Snackbar.LENGTH_LONG).setAction("Action", null)
//     setActionTextColor(Color.BLUE).show()

    Snackbar.make(view, "Replace with your own action",
        Snackbar.LENGTH_LONG).show()
}