package com.chema.ptoyecto_tfg.rv

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.chema.ptoyecto_tfg.R
import com.chema.ptoyecto_tfg.models.Post

class AdapterRvPostAritstMuro(
    private val context: Context,
    private val imgPosts: List<Bitmap>,
) : RecyclerView.Adapter<AdapterRvPostAritstMuro.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = imgPosts[position]
        holder.postContentIv.isVisible = false
        holder.postContentIv.setImageBitmap(post)


        holder.postItemFl.setOnClickListener(View.OnClickListener {
            //goToDetail(post)
            //this.notifyDataSetChanged()
        })
    }

    override fun getItemCount(): Int {
        return imgPosts.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val postContentIv: ImageView = itemView.findViewById(R.id.postContentIv)
        val postItemFl: FrameLayout = itemView.findViewById(R.id.postItemFl)
    }

    private fun goToDetail(post: Post?) {
        if (post == null) {
            return
        }
        //val intent = Intent(this.context, DetailActivity::class.java)
        //intent.putExtra("postContent", post.content)
        //this.context.startActivity(intent)
    }
}