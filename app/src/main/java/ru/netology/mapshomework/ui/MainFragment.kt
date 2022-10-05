package ru.netology.mapshomework.ui

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collectLatest
import ru.netology.mapshomework.R
import ru.netology.mapshomework.base.BaseFragment
import ru.netology.mapshomework.core.viewmodel.ViewModelFactory
import ru.netology.mapshomework.databinding.FragmentMainBinding
import ru.netology.mapshomework.utils.getAppComponent
import ru.netology.mapshomework.utils.setVisibility
import javax.inject.Inject


class MainFragment : BaseFragment<FragmentMainBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentMainBinding
        get() = FragmentMainBinding::inflate

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val viewModel: MarkersViewModel by activityViewModels {
        factory
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getAppComponent().inject(this)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() = with(binding) {
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        mainNavController?.apply {
            val appBarConfiguration = AppBarConfiguration(graph)
            toolbar.setupWithNavController(this, appBarConfiguration)
        }
        val adapter = MarksAdapter(object : MarksListener {
            override fun onRemove(id: Long) {
                viewModel.remove(id)
            }

            override fun onEdit(id: Long, text: String) {
                val edit = EditText(requireContext())
                edit.layoutParams = ViewGroup.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                edit.setText(text)
                AlertDialog.Builder(requireContext())
                    .setTitle(text)
                    .setMessage(R.string.add_marks_description)
                    .setView(edit)
                    .setIcon(R.drawable.map_marker_radius)
                    .setPositiveButton(
                        R.string.add_marks_confirm
                    ) { _, _ ->
                        val newText = edit.text.toString()
                        if (newText.isNotBlank()) {
                            viewModel.edit(id, newText)
                        }
                    }
                    .setNegativeButton(R.string.cancel) { dialog, _ ->
                        dialog.cancel()
                    }
                    .show()
            }

            override fun onTap(id: Long?) {
                if (id != null) {
                    mainNavController?.navigate(
                        MainFragmentDirections.actionMainFragmentToMapsFragment(
                            id
                        )
                    )
                }
            }
        })
        rvMarks.adapter = adapter

        lifecycleScope.launchWhenCreated {
            viewModel.marks.collectLatest { markers ->
                adapter.submitData(markers)
            }
        }
        adapter.addLoadStateListener { _ ->
            try {
                tvEmpty.setVisibility(adapter.itemCount <= 0)
            } catch (_: IllegalArgumentException) {

            }
        }
    }

    override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_mark -> {
                mainNavController?.navigate(
                    MainFragmentDirections.actionMainFragmentToMapsFragment(
                        0L
                    )
                )
                true
            }
            else -> super.onMenuItemSelected(item)
        }
    }

}