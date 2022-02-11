package com.example.polyenergy.ui.favorite

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.polyenergy.LATITUDE
import com.example.polyenergy.LONGITUDE
import com.example.polyenergy.R
import com.example.polyenergy.USER_COOKIE
import com.example.polyenergy.databinding.FragmentFavoriteBinding
import com.example.polyenergy.domain.ChargeInfo

class FavoriteFragment : Fragment() {
    private val viewModel: FavoriteViewModel by viewModels()

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private var lastDeleted: ChargeInfo? = null

    private var items: MutableList<ChargeInfo> = mutableListOf()
    private val adapter by lazy {
        ChargeAdapter(items, ::onDeleteClickListener, ::onClickListener)
    }
    private var iterator: Int = 0

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val divider = DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        recyclerView = binding.favorites
        viewManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = viewManager
        recyclerView.addItemDecoration(divider)
        recyclerView.adapter = adapter

        val cookie = requireContext().getSharedPreferences(
            requireContext().getString(R.string.app_name),
            Context.MODE_PRIVATE
        ).getString(USER_COOKIE, null)
        if (cookie != null) {
            viewModel.getFavorites(cookie)
        } else {
            Toast.makeText(
                requireContext(),
                "Problème de connexion, reconnectez-vous",
                Toast.LENGTH_SHORT
            ).show()
        }

        viewModel.result.observe(viewLifecycleOwner) {
            if (!it.success.isNullOrEmpty()) {
                items.remove(lastDeleted)
                adapter.items = items
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.favorites.observe(viewLifecycleOwner) {
            items = it as MutableList<ChargeInfo>
            adapter.items = it
            adapter.notifyDataSetChanged()
        }
    }

    private fun onDeleteClickListener(charge: ChargeInfo) {
        val cookie = requireContext().getSharedPreferences(
            this.getString(R.string.app_name),
            Context.MODE_PRIVATE
        ).getString(USER_COOKIE, null)
        if (cookie != null) {
            lastDeleted = charge
            viewModel.deleteFavorite(charge, cookie)
        }

    }

    private fun onClickListener(charge: ChargeInfo) {
        val bundle = Bundle()
        bundle.putDouble(LATITUDE, charge.addressInfo.latitude)
        bundle.putDouble(LONGITUDE, charge.addressInfo.longitude)
        Navigation.findNavController(binding.root).navigate(R.id.nav_carmap, bundle)
    }

}