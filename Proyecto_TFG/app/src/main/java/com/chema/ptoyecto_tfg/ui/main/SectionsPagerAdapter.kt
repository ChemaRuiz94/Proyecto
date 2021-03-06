package com.chema.ptoyecto_tfg.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.chema.ptoyecto_tfg.R
import com.chema.ptoyecto_tfg.navigation.artist.ui.BasicUserProfile.BasicUserProfileFragment
import com.chema.ptoyecto_tfg.navigation.artist.ui.BasicUserSearch.BasicUserSearchFragment
import com.chema.ptoyecto_tfg.navigation.artist.ui.Citas.ChatsFragment
import com.chema.ptoyecto_tfg.navigation.artist.ui.favorites.FavoritesFragment

private val TAB_TITLES = arrayOf(

    R.string.menu_search,
    R.string.menu_citas,
    R.string.menu_favorites,
    R.string.menu_my_profile,
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        when(position){
            0 -> {
                return BasicUserSearchFragment()
            }
            1 -> {
                return ChatsFragment()
            }
            2 -> {
                return FavoritesFragment()
            }
            3 -> {
                return BasicUserProfileFragment()
            }

        }
        return PlaceholderFragment.newInstance(position + 1)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        // Show 2 total pages.
        return 4
    }
}